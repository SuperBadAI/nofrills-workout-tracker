package com.gymlog.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gymlog.data.local.database.entity.ExerciseEntity
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

    /** Inserts a new exercise name and returns generated id. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exerciseEntity: ExerciseEntity): Long
}
