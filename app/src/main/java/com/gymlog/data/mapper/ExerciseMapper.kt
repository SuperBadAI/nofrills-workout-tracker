package com.gymlog.data.mapper

import com.gymlog.data.local.database.entity.ExerciseEntity
import com.gymlog.domain.model.Exercise

/** Converts exercise entity and domain models. */
fun ExerciseEntity.toDomain(): Exercise = Exercise(id = id, name = name, createdAt = createdAt)

/** Converts exercise domain model to entity model. */
fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(id = id, name = name, createdAt = createdAt)
