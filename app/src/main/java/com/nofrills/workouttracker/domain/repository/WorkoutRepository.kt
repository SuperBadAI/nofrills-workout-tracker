package com.nofrills.workouttracker.domain.repository

import android.net.Uri
import com.nofrills.workouttracker.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

/** Contract for writing and reading workout sessions. */
interface WorkoutRepository {
    /** Returns the most recent session for one exercise. */
    suspend fun getLastSessionForExercise(exerciseId: Long, userName: String): Result<WorkoutSession?>

    /** Saves one session and returns its generated id. */
    suspend fun saveWorkoutSession(session: WorkoutSession): Result<Long>

    /** Exports full history to a timestamped CSV file. */
    suspend fun exportAllSessionsToCsv(userName: String): Result<Uri?>

    /** Emits sorted distinct usernames that have saved sessions. */
    fun observeUserNamesWithData(): Flow<List<String>>
}
