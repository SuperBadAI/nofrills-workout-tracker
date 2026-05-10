package com.nofrills.workouttracker.data.repository

import android.net.Uri
import android.util.Log
import com.nofrills.workouttracker.di.IoDispatcher
import com.nofrills.workouttracker.data.local.csv.CsvManager
import com.nofrills.workouttracker.data.local.database.dao.WorkoutSessionDao
import com.nofrills.workouttracker.data.local.database.dao.WorkoutSetDao
import com.nofrills.workouttracker.data.mapper.toDomain
import com.nofrills.workouttracker.data.mapper.toEntity
import com.nofrills.workouttracker.data.mapper.toSessionEntity
import com.nofrills.workouttracker.domain.model.WorkoutSession
import com.nofrills.workouttracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
    override suspend fun getLastSessionForExercise(exerciseId: Long, userName: String): Result<WorkoutSession?> = withContext(ioDispatcher) {
        runCatching {
            workoutSessionDao.getLastSessionWithExerciseAndSets(exerciseId, userName)?.toDomain()
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
    override suspend fun exportAllSessionsToCsv(userName: String): Result<Uri?> = withContext(ioDispatcher) {
        runCatching {
            val sessions = workoutSessionDao.getAllSessionsWithExerciseAndSetsOnce(userName).map { it.toDomain() }
            csvManager.exportAllData(sessions, userName)
        }
    }

    override fun observeUserNamesWithData(): Flow<List<String>> =
        workoutSessionDao.observeDistinctUserNames()

    /** Deletes one profile's saved sessions; exercises are shared names and remain available for other users. */
    override suspend fun deleteUserProfile(userName: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val normalized = userName.trim()
            require(normalized.isNotBlank()) { "Choose a profile to delete" }
            workoutSessionDao.deleteSessionsForUser(normalized)
            Unit
        }
    }
}
