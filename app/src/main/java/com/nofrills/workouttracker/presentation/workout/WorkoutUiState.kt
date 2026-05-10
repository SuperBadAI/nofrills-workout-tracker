package com.nofrills.workouttracker.presentation.workout

import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.model.WorkoutSession

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
    /** Editable label while logging; kept in sync when an exercise is selected. */
    val exerciseNameDraft: String = "",
    val isRenamingExercise: Boolean = false,
    val previousSession: WorkoutSession? = null,
    val currentSets: List<MutableSetInput> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showAbandonDialog: Boolean = false,
    val successMessage: String? = null,
    val userNamesWithData: List<String> = emptyList(),
    val deleteProfileCandidate: String? = null,
    val isDeletingProfile: Boolean = false,
    val showShareCsvDialog: Boolean = false,
    val shareCsvSelectedUser: String = "",
    val isExportingCsv: Boolean = false
)
