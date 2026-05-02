package com.nofrills.workouttracker.domain.model

/** Represents a lift or movement users can track repeatedly. */
data class Exercise(
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
