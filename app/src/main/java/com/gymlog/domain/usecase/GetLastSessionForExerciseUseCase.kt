package com.gymlog.domain.usecase

import com.gymlog.domain.model.WorkoutSession
import com.gymlog.domain.repository.WorkoutRepository

/** Loads the last completed session for progressive overload hints. */
class GetLastSessionForExerciseUseCase(
    private val repository: WorkoutRepository
) {
    /** Returns latest session for an exercise, or null when none exists. */
    suspend operator fun invoke(exerciseId: Long, userName: String): Result<WorkoutSession?> =
        repository.getLastSessionForExercise(exerciseId, userName)
}
