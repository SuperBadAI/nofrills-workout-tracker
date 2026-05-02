package com.gymlog.data.repository

import com.gymlog.data.local.database.dao.ExerciseDao
import com.gymlog.data.local.database.entity.ExerciseEntity
import com.gymlog.di.IoDispatcher
import com.gymlog.data.mapper.toDomain
import com.gymlog.domain.model.Exercise
import com.gymlog.domain.repository.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** ExerciseRepository backed by Room DAO. */
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ExerciseRepository {
    /** Emits all exercises from database sorted by name. */
    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAllExercises().map { entities -> entities.map { it.toDomain() } }

    /** Emits exercises matching query token sorted by name. */
    override fun searchExercises(query: String): Flow<List<Exercise>> =
        exerciseDao.searchExercises(query).map { entities -> entities.map { it.toDomain() } }

    /** Returns existing exercise by exact name or inserts a new one. */
    override suspend fun getOrCreateExercise(name: String): Result<Exercise> = withContext(ioDispatcher) {
        runCatching {
            val normalized = name.trim()
            val existing = exerciseDao.findByExactName(normalized)
            if (existing != null) return@runCatching existing.toDomain()

            val rowId = exerciseDao.insertExercise(
                ExerciseEntity(name = normalized, createdAt = System.currentTimeMillis())
            )
            val inserted = if (rowId > 0) {
                exerciseDao.findByExactName(normalized)
            } else {
                exerciseDao.findByExactName(normalized)
            }
            requireNotNull(inserted) { "Exercise insert failed for name: $normalized" }
            inserted.toDomain()
        }
    }
}
