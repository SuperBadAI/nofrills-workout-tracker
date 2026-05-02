package com.gymlog.domain.model

/** Represents one completed workout for a single exercise. */
data class WorkoutSession(
    val id: Long = 0,
    val exercise: Exercise,
    val sets: List<WorkoutSet>,
    val completedAt: Long = System.currentTimeMillis()
)
