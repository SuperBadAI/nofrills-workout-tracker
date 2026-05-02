package com.nofrills.workouttracker.data.mapper

import com.nofrills.workouttracker.data.local.database.entity.ExerciseEntity
import com.nofrills.workouttracker.domain.model.Exercise

/** Converts exercise entity and domain models. */
fun ExerciseEntity.toDomain(): Exercise = Exercise(id = id, name = name, createdAt = createdAt)

/** Converts exercise domain model to entity model. */
fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(id = id, name = name, createdAt = createdAt)
