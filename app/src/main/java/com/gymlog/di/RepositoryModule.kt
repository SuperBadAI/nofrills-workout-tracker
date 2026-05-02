package com.gymlog.di

import com.gymlog.data.repository.ExerciseRepositoryImpl
import com.gymlog.data.repository.WorkoutRepositoryImpl
import com.gymlog.domain.repository.ExerciseRepository
import com.gymlog.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Binds repository implementations to domain interfaces. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    /** Binds ExerciseRepository implementation. */
    @Binds
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository

    /** Binds WorkoutRepository implementation. */
    @Binds
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository
}
