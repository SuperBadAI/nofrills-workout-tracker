package com.gymlog.domain.usecase

import com.gymlog.domain.model.Exercise
import com.gymlog.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow

/** Searches exercises with minimum one-character query behavior. */
class SearchExercisesUseCase(
    private val repository: ExerciseRepository
) {
    /** Returns all exercises when query is blank; filters when query has one or more chars. */
    operator fun invoke(query: String): Flow<List<Exercise>> {
        return if (query.isBlank()) repository.getAllExercises() else repository.searchExercises(query)
    }
}
