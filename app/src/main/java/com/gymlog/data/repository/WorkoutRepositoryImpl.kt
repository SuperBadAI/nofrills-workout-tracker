package com.gymlog.data.repository

import android.net.Uri
import android.util.Log
import com.gymlog.di.IoDispatcher
import com.gymlog.data.local.csv.CsvManager
import com.gymlog.data.local.database.dao.WorkoutSessionDao
import com.gymlog.data.local.database.dao.WorkoutSetDao
import com.gymlog.data.mapper.toDomain
import com.gymlog.data.mapper.toEntity
import com.gymlog.data.mapper.toSessionEntity
import com.gymlog.domain.model.WorkoutSession
import com.gymlog.domain.repository.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** WorkoutRepository backed by Room and CSV manager. */
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutSessionDao: WorkoutSessionDao,
    private val workoutSetDao: WorkoutSetDao,
    private val csvManager: CsvManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WorkoutRepository {
    /** Returns latest session for one exercise including all sets. */
    override suspend fun getLastSessionForExercise(exerciseId: Long): Result<WorkoutSession?> = withContext(ioDispatcher) {
        runCatching {
            workoutSessionDao.getLastSessionWithExerciseAndSets(exerciseId)?.toDomain()
        }
    }

    /** Saves one session to Room, then appends to running CSV. */
    override suspend fun saveWorkoutSession(session: WorkoutSession): Result<Long> = withContext(ioDispatcher) {
        runCatching {
            val sessionId = workoutSessionDao.insertSession(session.toSessionEntity())
            val setEntities = session.sets.map { it.toEntity(sessionId) }
            workoutSetDao.insertSets(setEntities)

            kotlin.runCatching {
                csvManager.appendSession(session.copy(id = sessionId))
            }.onFailure { error ->
                Log.e("WorkoutRepositoryImpl", "CSV append failed but DB save succeeded", error)
            }

            sessionId
        }
    }

    /** Exports all sessions to timestamped CSV and returns output Uri. */
    override suspend fun exportAllSessionsToCsv(): Result<Uri?> = withContext(ioDispatcher) {
        runCatching {
            val sessions = workoutSessionDao.getAllSessionsWithExerciseAndSetsOnce().map { it.toDomain() }
            csvManager.exportAllData(sessions)
        }
    }
}
