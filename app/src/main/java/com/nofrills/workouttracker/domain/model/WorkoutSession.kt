package com.nofrills.workouttracker.domain.model

/** Represents one completed workout for a single exercise. */
data class WorkoutSession(
    val id: Long = 0,
    val userName: String,
    val exercise: Exercise,
    val sets: List<WorkoutSet>,
    val completedAt: Long = System.currentTimeMillis()
)
