package com.gymlog.di

import android.content.Context
import androidx.room.Room
import com.gymlog.data.local.database.GymLogDatabase
import com.gymlog.data.local.database.dao.ExerciseDao
import com.gymlog.data.local.database.dao.WorkoutSessionDao
import com.gymlog.data.local.database.dao.WorkoutSetDao
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
    fun provideDatabase(@ApplicationContext context: Context): GymLogDatabase =
        Room.databaseBuilder(context, GymLogDatabase::class.java, "gymlog.db")
            .fallbackToDestructiveMigration()
            .build()

    /** Provides Exercise DAO. */
    @Provides
    fun provideExerciseDao(db: GymLogDatabase): ExerciseDao = db.exerciseDao()

    /** Provides WorkoutSession DAO. */
    @Provides
    fun provideWorkoutSessionDao(db: GymLogDatabase): WorkoutSessionDao = db.workoutSessionDao()

    /** Provides WorkoutSet DAO. */
    @Provides
    fun provideWorkoutSetDao(db: GymLogDatabase): WorkoutSetDao = db.workoutSetDao()
}
