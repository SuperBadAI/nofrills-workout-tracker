package com.nofrills.workouttracker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nofrills.workouttracker.data.local.database.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/** DAO for exercise lookup and autocomplete. */
@Dao
interface ExerciseDao {
    /** Emits all exercises sorted alphabetically. */
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    /** Emits exercises whose name contains the query token. */
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchExercises(query: String): Flow<List<ExerciseEntity>>

    /** Finds one exercise by exact name, case-insensitive. */
    @Query("SELECT * FROM exercises WHERE lower(name) = lower(:name) LIMIT 1")
    suspend fun findByExactName(name: String): ExerciseEntity?

    /** Loads one exercise by primary key. */
    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): ExerciseEntity?

    /** Inserts a new exercise name and returns generated id. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exerciseEntity: ExerciseEntity): Long

    /** Updates display name (must stay unique; callers should validate). */
    @Query("UPDATE exercises SET name = :name WHERE id = :id")
    suspend fun updateName(id: Long, name: String)

    /** Removes an exercise row after its sessions have been moved elsewhere. */
    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    /** Moves all sessions from one exercise to another before deleting the now-empty duplicate row. */
    @Transaction
    suspend fun mergeExerciseInto(sourceExerciseId: Long, targetExerciseId: Long) {
        moveSessionsToExercise(sourceExerciseId, targetExerciseId)
        deleteById(sourceExerciseId)
    }

    /** Re-points historical sessions during an exercise-name merge. */
    @Query("UPDATE workout_sessions SET exercise_id = :targetExerciseId WHERE exercise_id = :sourceExerciseId")
    suspend fun moveSessionsToExercise(sourceExerciseId: Long, targetExerciseId: Long): Int
}
