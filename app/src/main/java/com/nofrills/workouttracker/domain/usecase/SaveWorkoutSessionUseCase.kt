package com.nofrills.workouttracker.domain.usecase

import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.model.WorkoutSession
import com.nofrills.workouttracker.domain.model.WorkoutSet
import com.nofrills.workouttracker.domain.repository.WorkoutRepository

/** Persists a completed workout session and appends it to CSV log. */
class SaveWorkoutSessionUseCase(
    private val repository: WorkoutRepository
) {
    /** Saves one session for the provided exercise and list of sets. */
    suspend operator fun invoke(
        userName: String,
        exercise: Exercise,
        sets: List<WorkoutSet>
    ): Result<Long> {
        return repository.saveWorkoutSession(
            WorkoutSession(userName = userName, exercise = exercise, sets = sets)
        )
    }
}
