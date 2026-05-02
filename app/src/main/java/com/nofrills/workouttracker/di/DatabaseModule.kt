package com.nofrills.workouttracker.di

import android.content.Context
import androidx.room.Room
import com.nofrills.workouttracker.data.local.database.WorkoutTrackerDatabase
import com.nofrills.workouttracker.data.local.database.dao.ExerciseDao
import com.nofrills.workouttracker.data.local.database.dao.WorkoutSessionDao
import com.nofrills.workouttracker.data.local.database.dao.WorkoutSetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Provides Room database and DAOs. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /** Provides app Room database singleton. */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WorkoutTrackerDatabase =
        Room.databaseBuilder(context, WorkoutTrackerDatabase::class.java, "nofrills_workout_tracker.db")
            .fallbackToDestructiveMigration()
            .build()

    /** Provides Exercise DAO. */
    @Provides
    fun provideExerciseDao(db: WorkoutTrackerDatabase): ExerciseDao = db.exerciseDao()

    /** Provides WorkoutSession DAO. */
    @Provides
    fun provideWorkoutSessionDao(db: WorkoutTrackerDatabase): WorkoutSessionDao = db.workoutSessionDao()

    /** Provides WorkoutSet DAO. */
    @Provides
    fun provideWorkoutSetDao(db: WorkoutTrackerDatabase): WorkoutSetDao = db.workoutSetDao()
}
