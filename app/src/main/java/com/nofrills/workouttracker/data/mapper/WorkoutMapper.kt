package com.nofrills.workouttracker.data.mapper

import com.nofrills.workouttracker.data.local.database.entity.WorkoutSessionEntity
import com.nofrills.workouttracker.data.local.database.entity.WorkoutSetEntity
import com.nofrills.workouttracker.data.local.database.model.SessionWithExerciseAndSets
import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.model.WorkoutSession
import com.nofrills.workouttracker.domain.model.WorkoutSet

/** Converts workout set domain to entity for one session id. */
fun WorkoutSet.toEntity(sessionId: Long): WorkoutSetEntity = WorkoutSetEntity(
    id = id,
    sessionId = sessionId,
    setNumber = setNumber,
    reps = reps,
    weightKg = weightKg,
    isDropSet = isDropSet,
    parentSetId = parentSetId
)

/** Converts workout set entity to domain model. */
fun WorkoutSetEntity.toDomain(): WorkoutSet = WorkoutSet(
    id = id,
    setNumber = setNumber,
    reps = reps,
    weightKg = weightKg,
    isDropSet = isDropSet,
    parentSetId = parentSetId
)

/** Converts full export aggregate to domain session. */
fun SessionWithExerciseAndSets.toDomain(): WorkoutSession = WorkoutSession(
    id = session.id,
    userName = session.userName,
    exercise = Exercise(
        id = exercise.id,
        name = exercise.name,
        createdAt = exercise.createdAt
    ),
    sets = sets.sortedBy { it.setNumber }.map { it.toDomain() },
    completedAt = session.completedAtMillis
)

/** Builds a session entity from exercise and timestamp. */
fun WorkoutSession.toSessionEntity(): WorkoutSessionEntity = WorkoutSessionEntity(
    id = id,
    userName = userName,
    exerciseId = exercise.id,
    completedAtMillis = completedAt
)
