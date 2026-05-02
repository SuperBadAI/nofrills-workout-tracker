package com.nofrills.workouttracker.data.local.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.nofrills.workouttracker.data.local.database.entity.WorkoutSessionEntity
import com.nofrills.workouttracker.data.local.database.entity.WorkoutSetEntity

/** Aggregate model representing a session and its sets. */
data class SessionWithSets(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(parentColumn = "id", entityColumn = "session_id")
    val sets: List<WorkoutSetEntity>
)
