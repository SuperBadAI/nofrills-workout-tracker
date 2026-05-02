package com.nofrills.workouttracker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nofrills.workouttracker.data.local.database.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

/** DAO for session set rows. */
@Dao
interface WorkoutSetDao {
    /** Inserts all set rows for a session. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSets(sets: List<WorkoutSetEntity>)

    /** Emits all sets for one session sorted by set number. */
    @Query("SELECT * FROM workout_sets WHERE session_id = :sessionId ORDER BY set_number ASC")
    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSetEntity>>
}
