package com.nofrills.workouttracker.di

import com.nofrills.workouttracker.domain.repository.ExerciseRepository
import com.nofrills.workouttracker.domain.repository.WorkoutRepository
import com.nofrills.workouttracker.domain.usecase.ExportToCsvUseCase
import com.nofrills.workouttracker.domain.usecase.GetLastSessionForExerciseUseCase
import com.nofrills.workouttracker.domain.usecase.GetOrCreateExerciseUseCase
import com.nofrills.workouttracker.domain.usecase.SaveWorkoutSessionUseCase
import com.nofrills.workouttracker.domain.usecase.SearchExercisesUseCase
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
