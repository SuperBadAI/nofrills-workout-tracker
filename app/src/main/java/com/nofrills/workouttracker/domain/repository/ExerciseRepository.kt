package com.nofrills.workouttracker.domain.repository

import com.nofrills.workouttracker.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

/** Contract for searching and creating exercises. */
interface ExerciseRepository {
    /** Returns all exercises sorted by name. */
    fun getAllExercises(): Flow<List<Exercise>>

    /** Returns filtered exercises sorted by name. */
    fun searchExercises(query: String): Flow<List<Exercise>>

    /** Looks up or creates an exercise by name. */
    suspend fun getOrCreateExercise(name: String): Result<Exercise>

    /** Renames an existing exercise; fails if [newName] is blank or collides with another exercise. */
    suspend fun renameExercise(id: Long, newName: String): Result<Exercise>
}
