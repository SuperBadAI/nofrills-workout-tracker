package com.nofrills.workouttracker.domain.usecase

import com.nofrills.workouttracker.domain.model.WorkoutSession
import com.nofrills.workouttracker.domain.repository.WorkoutRepository

/** Loads the last completed session for progressive overload hints. */
class GetLastSessionForExerciseUseCase(
    private val repository: WorkoutRepository
) {
    /** Returns latest session for an exercise, or null when none exists. */
    suspend operator fun invoke(exerciseId: Long, userName: String): Result<WorkoutSession?> =
        repository.getLastSessionForExercise(exerciseId, userName)
}
