package com.nofrills.workouttracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nofrills.workouttracker.data.local.database.dao.ExerciseDao
import com.nofrills.workouttracker.data.local.database.dao.WorkoutSessionDao
import com.nofrills.workouttracker.data.local.database.dao.WorkoutSetDao
import com.nofrills.workouttracker.data.local.database.entity.ExerciseEntity
import com.nofrills.workouttracker.data.local.database.entity.WorkoutSessionEntity
import com.nofrills.workouttracker.data.local.database.entity.WorkoutSetEntity

/** Room database holding exercises and completed workout sessions for **No Frills Workout Tracker**. */
@Database(
    entities = [ExerciseEntity::class, WorkoutSessionEntity::class, WorkoutSetEntity::class],
    version = 2,
    exportSchema = true
)
abstract class WorkoutTrackerDatabase : RoomDatabase() {
    /** Exercise DAO provider. */
    abstract fun exerciseDao(): ExerciseDao

    /** Workout session DAO provider. */
    abstract fun workoutSessionDao(): WorkoutSessionDao

    /** Workout set DAO provider. */
    abstract fun workoutSetDao(): WorkoutSetDao
}
