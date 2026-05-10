package com.nofrills.workouttracker.data.repository

import com.nofrills.workouttracker.data.local.database.dao.ExerciseDao
import com.nofrills.workouttracker.data.local.database.entity.ExerciseEntity
import com.nofrills.workouttracker.di.IoDispatcher
import com.nofrills.workouttracker.data.mapper.toDomain
import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.repository.ExerciseRepository
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

    override suspend fun renameExercise(id: Long, newName: String): Result<Exercise> = withContext(ioDispatcher) {
        runCatching {
            val normalized = newName.trim()
            require(normalized.isNotBlank()) { "Exercise name cannot be empty" }
            val self = exerciseDao.findById(id) ?: error("Exercise not found")
            if (self.name.equals(normalized, ignoreCase = true)) return@runCatching self.toDomain()
            val clash = exerciseDao.findByExactName(normalized)
            if (clash != null && clash.id != id) {
                exerciseDao.mergeExerciseInto(sourceExerciseId = id, targetExerciseId = clash.id)
                return@runCatching clash.toDomain()
            }
            exerciseDao.updateName(id, normalized)
            exerciseDao.findById(id)!!.toDomain()
        }
    }
}
