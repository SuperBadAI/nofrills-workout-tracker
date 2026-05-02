package com.gymlog.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gymlog.data.local.database.entity.WorkoutSessionEntity
import com.gymlog.data.local.database.model.SessionWithExerciseAndSets
import com.gymlog.data.local.database.model.SessionWithSets
import kotlinx.coroutines.flow.Flow

/** DAO for workout session records. */
@Dao
interface WorkoutSessionDao {
    /** Inserts one session row and returns generated id. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    /** Returns latest session and attached sets for an exercise. */
    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE exercise_id = :exerciseId ORDER BY completed_at_millis DESC LIMIT 1")
    suspend fun getLastSessionWithSets(exerciseId: Long): SessionWithSets?

    /** Returns latest session with exercise metadata and attached sets. */
    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE exercise_id = :exerciseId ORDER BY completed_at_millis DESC LIMIT 1")
    suspend fun getLastSessionWithExerciseAndSets(exerciseId: Long): SessionWithExerciseAndSets?

    /** Emits all sessions joined with exercises and sets for export. */
    @Transaction
    @Query("SELECT * FROM workout_sessions ORDER BY completed_at_millis DESC")
    fun getAllSessionsWithExerciseAndSets(): Flow<List<SessionWithExerciseAndSets>>

    /** Returns all sessions with exercise and sets in one snapshot. */
    @Transaction
    @Query("SELECT * FROM workout_sessions ORDER BY completed_at_millis DESC")
    suspend fun getAllSessionsWithExerciseAndSetsOnce(): List<SessionWithExerciseAndSets>
}
