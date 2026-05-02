package com.gymlog.di

import com.gymlog.domain.repository.ExerciseRepository
import com.gymlog.domain.repository.WorkoutRepository
import com.gymlog.domain.usecase.ExportToCsvUseCase
import com.gymlog.domain.usecase.GetLastSessionForExerciseUseCase
import com.gymlog.domain.usecase.GetOrCreateExerciseUseCase
import com.gymlog.domain.usecase.SaveWorkoutSessionUseCase
import com.gymlog.domain.usecase.SearchExercisesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Provides domain use case objects. */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    /** Provides SearchExercisesUseCase. */
    @Provides
    fun provideSearchExercisesUseCase(repository: ExerciseRepository) = SearchExercisesUseCase(repository)

    /** Provides GetOrCreateExerciseUseCase. */
    @Provides
    fun provideGetOrCreateExerciseUseCase(repository: ExerciseRepository) = GetOrCreateExerciseUseCase(repository)

    /** Provides GetLastSessionForExerciseUseCase. */
    @Provides
    fun provideGetLastSessionForExerciseUseCase(repository: WorkoutRepository) =
        GetLastSessionForExerciseUseCase(repository)

    /** Provides SaveWorkoutSessionUseCase. */
    @Provides
    fun provideSaveWorkoutSessionUseCase(repository: WorkoutRepository) = SaveWorkoutSessionUseCase(repository)

    /** Provides ExportToCsvUseCase. */
    @Provides
    fun provideExportToCsvUseCase(repository: WorkoutRepository) = ExportToCsvUseCase(repository)
}
