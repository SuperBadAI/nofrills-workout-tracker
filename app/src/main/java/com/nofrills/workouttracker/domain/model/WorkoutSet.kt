package com.nofrills.workouttracker.domain.model

/** Represents one set within a workout session. */
data class WorkoutSet(
    val id: Long = 0,
    val setNumber: Int,
    val reps: Int,
    val weightKg: Float,
    val isDropSet: Boolean = false,
    val parentSetId: Long? = null
)
