package com.nofrills.workouttracker.domain.usecase

import com.nofrills.workouttracker.domain.repository.WorkoutRepository
import javax.inject.Inject

/** Deletes all saved workout sessions for one user profile. */
class DeleteUserProfileUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(userName: String): Result<Unit> =
        workoutRepository.deleteUserProfile(userName)
}
