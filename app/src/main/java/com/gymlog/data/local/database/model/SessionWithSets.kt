package com.gymlog.data.local.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.gymlog.data.local.database.entity.WorkoutSessionEntity
import com.gymlog.data.local.database.entity.WorkoutSetEntity

/** Aggregate model representing a session and its sets. */
data class SessionWithSets(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(parentColumn = "id", entityColumn = "session_id")
    val sets: List<WorkoutSetEntity>
)
