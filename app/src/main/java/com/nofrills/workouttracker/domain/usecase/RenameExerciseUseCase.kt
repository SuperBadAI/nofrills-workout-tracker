package com.nofrills.workouttracker.domain.usecase

import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.repository.ExerciseRepository
import javax.inject.Inject

/** Persists a new display name for an existing exercise row. */
class RenameExerciseUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(id: Long, newName: String): Result<Exercise> =
        exerciseRepository.renameExercise(id, newName)
}
