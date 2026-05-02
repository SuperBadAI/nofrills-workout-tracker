package com.gymlog.domain.usecase

import android.net.Uri
import com.gymlog.domain.repository.WorkoutRepository

/** Exports all workout sessions to a standalone CSV file. */
class ExportToCsvUseCase(
    private val repository: WorkoutRepository
) {
    /** Writes historical sessions to CSV and returns the target Uri when successful. */
    suspend operator fun invoke(userName: String): Result<Uri?> =
        repository.exportAllSessionsToCsv(userName)
}
