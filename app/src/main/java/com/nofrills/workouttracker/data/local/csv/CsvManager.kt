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
import com.nofrills.workouttracker.domain.model.WorkoutSet
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
    /** v2 log so rows stay aligned with header that includes both kg and lbs. */
    private val fileName = "nofrills_workout_log_v2.csv"
    private val folderName = "Download/NoFrillsWorkoutTracker"
    private val header = "date,time,user,exercise,set_number,is_drop_set,parent_set,reps,weight_kg,weight_lbs\n"

    companion object {
        private const val KG_TO_LBS = 2.2046226218487757
    }

    /** Appends one completed session to running CSV log file. */
    suspend fun appendSession(session: WorkoutSession) = withContext(ioDispatcher) {
        val uri = getOrCreateLogFile() ?: return@withContext
        val output = context.contentResolver.openOutputStream(uri, "wa") ?: return@withContext
        output.bufferedWriter().use { writer ->
            session.sets.forEach { set ->
                val date = Date(session.completedAt)
                val label = if (set.isDropSet) "${set.setNumber}A" else set.setNumber.toString()
                writer.append(csvLine(session, date, label, set))
            }
        }
    }

    /**
     * Exports all historical sessions to a timestamped CSV file and returns its Uri.
     * [userNameForFile] is sanitized for the download filename (which user the export is for).
     */
    suspend fun exportAllData(sessions: List<WorkoutSession>, userNameForFile: String): Uri? = withContext(ioDispatcher) {
        val safe = sanitizeFileLabel(userNameForFile)
        val now = System.currentTimeMillis()
        val exportName = "nofrills_workout_${safe}_$now.csv"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
            val outFile = File(dir, exportName)
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

    private fun sanitizeFileLabel(raw: String): String {
        val collapsed = raw.trim().lowercase(Locale.US).replace(Regex("[^a-z0-9_-]+"), "_")
        val trimmed = collapsed.trim('_').replace(Regex("_+"), "_")
        return trimmed.take(40).ifBlank { "user" }
    }

    private fun writeSessionLines(writer: java.io.BufferedWriter, session: WorkoutSession) {
        val date = Date(session.completedAt)
        session.sets.forEach { set ->
            val label = if (set.isDropSet) "${set.setNumber}A" else set.setNumber.toString()
            writer.append(csvLine(session, date, label, set))
        }
    }

    private fun csvLine(session: WorkoutSession, date: Date, setLabel: String, set: WorkoutSet): String {
        val lbs = set.weightKg * KG_TO_LBS
        val kgFormatted = String.format(Locale.US, "%.1f", set.weightKg)
        val lbsFormatted = String.format(Locale.US, "%.1f", lbs)
        return "${dateFormat.format(date)},${timeFormat.format(date)},${session.userName},${session.exercise.name}," +
            "$setLabel,${set.isDropSet},${set.parentSetId ?: ""},${set.reps},$kgFormatted,$lbsFormatted\n"
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
