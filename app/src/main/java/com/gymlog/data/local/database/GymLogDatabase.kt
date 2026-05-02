package com.gymlog.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gymlog.data.local.database.dao.ExerciseDao
import com.gymlog.data.local.database.dao.WorkoutSessionDao
import com.gymlog.data.local.database.dao.WorkoutSetDao
import com.gymlog.data.local.database.entity.ExerciseEntity
import com.gymlog.data.local.database.entity.WorkoutSessionEntity
import com.gymlog.data.local.database.entity.WorkoutSetEntity

/** Primary Room database for GymLog entities. */
@Database(
    entities = [ExerciseEntity::class, WorkoutSessionEntity::class, WorkoutSetEntity::class],
    version = 1,
    exportSchema = true
)
abstract class GymLogDatabase : RoomDatabase() {
    /** Exercise DAO provider. */
    abstract fun exerciseDao(): ExerciseDao

    /** Workout session DAO provider. */
    abstract fun workoutSessionDao(): WorkoutSessionDao

    /** Workout set DAO provider. */
    abstract fun workoutSetDao(): WorkoutSetDao
}
