package com.gymlog.domain.usecase

import com.gymlog.domain.model.Exercise
import com.gymlog.domain.repository.ExerciseRepository

/** Retrieves an exercise by name or creates it if missing. */
class GetOrCreateExerciseUseCase(
    private val repository: ExerciseRepository
) {
    /** Returns an existing exercise or inserts a new one. */
    suspend operator fun invoke(name: String): Result<Exercise> = repository.getOrCreateExercise(name)
}
