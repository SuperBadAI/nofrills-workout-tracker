package com.nofrills.workouttracker.data.local.csv

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.nofrills.workouttracker.di.IoDispatcher
import com.nofrills.workouttracker.domain.model.WorkoutSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

/** Handles CSV logging and exports in shared Downloads storage. */
@Singleton
class CsvManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    private val fileName = "nofrills_workout_log.csv"
    private val folderName = "Download/NoFrillsWorkoutTracker"
    private val header = "date,time,user,exercise,set_number,is_drop_set,parent_set,reps,weight_kg\n"

    /** Appends one completed session to running CSV log file. */
    suspend fun appendSession(session: WorkoutSession) = withContext(ioDispatcher) {
        val uri = getOrCreateLogFile() ?: return@withContext
        val output = context.contentResolver.openOutputStream(uri, "wa") ?: return@withContext
        output.bufferedWriter().use { writer ->
            session.sets.forEach { set ->
                val date = Date(session.completedAt)
                val label = if (set.isDropSet) "${set.setNumber}A" else set.setNumber.toString()
                writer.append(
                    "${dateFormat.format(date)},${timeFormat.format(date)},${session.userName},${session.exercise.name}," +
                        "$label,${set.isDropSet},${set.parentSetId ?: ""},${set.reps},${set.weightKg}\n"
                )
            }
        }
    }

    /** Exports all historical sessions to a timestamped CSV file and returns its Uri. */
    suspend fun exportAllData(sessions: List<WorkoutSession>): Uri? = withContext(ioDispatcher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val now = System.currentTimeMillis()
            val exportName = "nofrills_workout_export_${now}.csv"
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, exportName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, "Download/NoFrillsWorkoutTracker")
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.bufferedWriter()?.use { writer ->
                    writer.append(header)
                    sessions.forEach { session -> writeSessionLines(writer, session) }
                }
            }
            uri
        } else {
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NoFrillsWorkoutTracker")
            if (!dir.exists()) dir.mkdirs()
            val outFile = File(dir, "nofrills_workout_export_${System.currentTimeMillis()}.csv")
            outFile.bufferedWriter().use { writer ->
                writer.append(header)
                sessions.forEach { session -> writeSessionLines(writer, session) }
            }
            Uri.fromFile(outFile)
        }
    }

    /** Returns current log file Uri, creating it with header if needed. */
    suspend fun getOrCreateLogFile(): Uri? = withContext(ioDispatcher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findExistingLogUriQ() ?: createLogUriQ()
        } else {
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val folder = File(downloads, "NoFrillsWorkoutTracker")
            if (!folder.exists()) folder.mkdirs()
            val file = File(folder, fileName)
            if (!file.exists()) {
                file.createNewFile()
                file.appendText(header)
            }
            Uri.fromFile(file)
        }
    }

    private fun writeSessionLines(writer: java.io.BufferedWriter, session: WorkoutSession) {
        val date = Date(session.completedAt)
        session.sets.forEach { set ->
            val label = if (set.isDropSet) "${set.setNumber}A" else set.setNumber.toString()
            writer.append(
                "${dateFormat.format(date)},${timeFormat.format(date)},${session.userName},${session.exercise.name}," +
                    "$label,${set.isDropSet},${set.parentSetId ?: ""},${set.reps},${set.weightKg}\n"
            )
        }
    }

    private fun createLogUriQ(): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, folderName)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.bufferedWriter()?.use { writer ->
                    writer.append(header)
                }
            } catch (ioException: IOException) {
                Log.e("CsvManager", "Failed writing CSV header", ioException)
            }
        }
        return uri
    }

    private fun findExistingLogUriQ(): Uri? {
        val projection = arrayOf(MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val args = arrayOf(fileName)
        context.contentResolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            args,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(idIndex)
                return Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id.toString())
            }
        }
        return null
    }
}
