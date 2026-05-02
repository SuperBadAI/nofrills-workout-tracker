package com.gymlog.presentation.workout

import com.gymlog.domain.model.Exercise
import com.gymlog.domain.model.WorkoutSession

/** UI model for one editable workout set row. */
data class MutableSetInput(
    val setId: Long = 0,
    val setNumber: Int,
    val reps: String = "",
    val weightKg: String = "",
    val isDropSet: Boolean = false,
    val parentSetId: Long? = null
)

/** Top-level screen states for one-screen app flow. */
enum class ScreenState { LOGIN, IDLE, EXERCISE_SELECTED }

/** Available weight units for input/display. */
enum class WeightUnit { KG, LBS }

/** Immutable state holder for workout screen render logic. */
data class WorkoutUiState(
    val screenState: ScreenState = ScreenState.LOGIN,
    val userName: String = "",
    val loginInput: String = "",
    val weightUnit: WeightUnit = WeightUnit.KG,
    val searchQuery: String = "",
    val searchResults: List<Exercise> = emptyList(),
    val selectedExercise: Exercise? = null,
    val previousSession: WorkoutSession? = null,
    val currentSets: List<MutableSetInput> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showAbandonDialog: Boolean = false,
    val successMessage: String? = null
)
