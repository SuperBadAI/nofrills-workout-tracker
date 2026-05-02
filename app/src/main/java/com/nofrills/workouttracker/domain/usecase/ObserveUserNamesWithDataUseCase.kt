package com.nofrills.workouttracker.domain.usecase

import com.nofrills.workouttracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Exposes usernames that have at least one workout row in the database. */
class ObserveUserNamesWithDataUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<String>> = repository.observeUserNamesWithData()
}
