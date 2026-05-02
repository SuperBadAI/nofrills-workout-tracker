package com.gymlog.data.local.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.gymlog.data.local.database.entity.ExerciseEntity
import com.gymlog.data.local.database.entity.WorkoutSessionEntity
import com.gymlog.data.local.database.entity.WorkoutSetEntity

/** Aggregate model for CSV export with exercise and sets. */
data class SessionWithExerciseAndSets(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(parentColumn = "exercise_id", entityColumn = "id")
    val exercise: ExerciseEntity,
    @Relation(parentColumn = "id", entityColumn = "session_id")
    val sets: List<WorkoutSetEntity>
)
