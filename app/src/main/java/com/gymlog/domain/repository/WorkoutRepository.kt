package com.gymlog.domain.repository

import android.net.Uri
import com.gymlog.domain.model.WorkoutSession

/** Contract for writing and reading workout sessions. */
interface WorkoutRepository {
    /** Returns the most recent session for one exercise. */
    suspend fun getLastSessionForExercise(exerciseId: Long): Result<WorkoutSession?>

    /** Saves one session and returns its generated id. */
    suspend fun saveWorkoutSession(session: WorkoutSession): Result<Long>

    /** Exports full history to a timestamped CSV file. */
    suspend fun exportAllSessionsToCsv(): Result<Uri?>
}
