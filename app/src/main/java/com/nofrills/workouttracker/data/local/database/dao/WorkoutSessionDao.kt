package com.nofrills.workouttracker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nofrills.workouttracker.data.local.database.entity.WorkoutSessionEntity
import com.nofrills.workouttracker.data.local.database.model.SessionWithExerciseAndSets
import kotlinx.coroutines.flow.Flow

/** DAO for workout session records. */
@Dao
interface WorkoutSessionDao {
    /** Inserts one session row and returns generated id. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    /** Returns latest session with exercise metadata and attached sets. */
    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE exercise_id = :exerciseId AND user_name = :userName ORDER BY completed_at_millis DESC LIMIT 1")
    suspend fun getLastSessionWithExerciseAndSets(exerciseId: Long, userName: String): SessionWithExerciseAndSets?

    /** Emits all sessions joined with exercises and sets for export. */
    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE user_name = :userName ORDER BY completed_at_millis DESC")
    fun getAllSessionsWithExerciseAndSets(userName: String): Flow<List<SessionWithExerciseAndSets>>

    /** Returns all sessions with exercise and sets in one snapshot. */
    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE user_name = :userName ORDER BY completed_at_millis DESC")
    suspend fun getAllSessionsWithExerciseAndSetsOnce(userName: String): List<SessionWithExerciseAndSets>
}
