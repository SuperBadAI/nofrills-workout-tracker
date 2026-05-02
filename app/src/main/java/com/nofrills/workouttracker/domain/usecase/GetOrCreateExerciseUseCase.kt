package com.nofrills.workouttracker.domain.usecase

import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.repository.ExerciseRepository

/** Retrieves an exercise by name or creates it if missing. */
class GetOrCreateExerciseUseCase(
    private val repository: ExerciseRepository
) {
    /** Returns an existing exercise or inserts a new one. */
    suspend operator fun invoke(name: String): Result<Exercise> = repository.getOrCreateExercise(name)
}
