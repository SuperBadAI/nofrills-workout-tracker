package com.gymlog.domain.usecase

import com.gymlog.domain.model.Exercise
import com.gymlog.domain.model.WorkoutSession
import com.gymlog.domain.model.WorkoutSet
import com.gymlog.domain.repository.WorkoutRepository

/** Persists a completed workout session and appends it to CSV log. */
class SaveWorkoutSessionUseCase(
    private val repository: WorkoutRepository
) {
    /** Saves one session for the provided exercise and list of sets. */
    suspend operator fun invoke(exercise: Exercise, sets: List<WorkoutSet>): Result<Long> {
        return repository.saveWorkoutSession(
            WorkoutSession(exercise = exercise, sets = sets)
        )
    }
}
