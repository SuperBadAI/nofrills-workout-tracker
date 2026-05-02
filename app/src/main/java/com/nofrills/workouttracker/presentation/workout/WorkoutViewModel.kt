package com.nofrills.workouttracker.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.model.WorkoutSet
import com.nofrills.workouttracker.domain.usecase.GetLastSessionForExerciseUseCase
import com.nofrills.workouttracker.domain.usecase.GetOrCreateExerciseUseCase
import com.nofrills.workouttracker.domain.usecase.SaveWorkoutSessionUseCase
import com.nofrills.workouttracker.domain.usecase.SearchExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** ViewModel that coordinates login, search, set edits, and save flow for **No Frills Workout Tracker**. */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val searchExercisesUseCase: SearchExercisesUseCase,
    private val getOrCreateExerciseUseCase: GetOrCreateExerciseUseCase,
    private val getLastSessionForExerciseUseCase: GetLastSessionForExerciseUseCase,
    private val saveWorkoutSessionUseCase: SaveWorkoutSessionUseCase
) : ViewModel() {

    private val mutableState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = mutableState.asStateFlow()

    private var searchJob: Job? = null

    /** Updates the login text field value. */
    fun onLoginInputChanged(value: String) {
        mutableState.update { it.copy(loginInput = value) }
    }

    /** Starts a user session for the provided username. */
    fun onLoginConfirmed() {
        val user = mutableState.value.loginInput.trim()
        if (user.isBlank()) {
            mutableState.update { it.copy(errorMessage = "Enter a username to continue") }
            return
        }
        mutableState.update {
            it.copy(
                userName = user,
                loginInput = user,
                screenState = ScreenState.IDLE,
                errorMessage = null
            )
        }
        observeSearch("")
    }

    /** Switches the weight unit used in set inputs. */
    fun onWeightUnitChanged(unit: WeightUnit) {
        mutableState.update { it.copy(weightUnit = unit) }
    }

    /** Updates query text and triggers reactive search. */
    fun onSearchQueryChanged(query: String) {
        mutableState.update { it.copy(searchQuery = query) }
        observeSearch(query)
    }

    private fun observeSearch(query: String) {
        if (mutableState.value.screenState == ScreenState.LOGIN) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchExercisesUseCase(query).collect { results ->
                mutableState.update { it.copy(searchResults = results) }
            }
        }
    }

    /** Selects an exercise and loads previous session hints for current user. */
    fun onExerciseSelected(exercise: Exercise) {
        val currentUser = mutableState.value.userName
        mutableState.update {
            it.copy(
                screenState = ScreenState.EXERCISE_SELECTED,
                selectedExercise = exercise,
                searchQuery = exercise.name,
                currentSets = listOf(MutableSetInput(setNumber = 1)),
                errorMessage = null
            )
        }
        viewModelScope.launch {
            getLastSessionForExerciseUseCase(exercise.id, currentUser)
                .onSuccess { session ->
                    mutableState.update { state -> state.copy(previousSession = session?.copy(exercise = exercise)) }
                }
                .onFailure { throwable ->
                    mutableState.update { it.copy(errorMessage = throwable.message ?: "Failed loading previous session") }
                }
        }
    }

    /** Creates a new exercise by name and selects it immediately. */
    fun onExerciseCreated(name: String) {
        viewModelScope.launch {
            getOrCreateExerciseUseCase(name)
                .onSuccess { exercise -> onExerciseSelected(exercise) }
                .onFailure { throwable ->
                    mutableState.update { it.copy(errorMessage = throwable.message ?: "Could not create exercise") }
                }
        }
    }

    /** Updates reps and weight values for one row by list index. */
    fun onSetUpdated(index: Int, reps: String, weightKg: String) {
        mutableState.update { state ->
            val updated = state.currentSets.mapIndexed { i, row ->
                if (i == index) row.copy(reps = reps, weightKg = weightKg) else row
            }
            state.copy(currentSets = updated)
        }
    }

    /** Appends a normal set row at end of list. */
    fun onAddSet() {
        mutableState.update { state ->
            val next = state.currentSets.count { !it.isDropSet } + 1
            state.copy(currentSets = state.currentSets + MutableSetInput(setNumber = next))
        }
    }

    /** Adds one drop set row under parent set index. */
    fun onAddDropSet(parentSetIndex: Int) {
        mutableState.update { state ->
            val parent = state.currentSets.getOrNull(parentSetIndex) ?: return@update state
            val row = MutableSetInput(
                setNumber = parent.setNumber,
                isDropSet = true,
                parentSetId = parent.setId.takeIf { it > 0 }
            )
            state.copy(currentSets = state.currentSets + row)
        }
    }

    /** Removes one set row by index when valid. */
    fun onRemoveSet(index: Int) {
        mutableState.update { state ->
            state.copy(currentSets = state.currentSets.filterIndexed { i, _ -> i != index })
        }
    }

    /** Persists current workout and resets to idle state. */
    fun onCompleteWorkout() {
        val current = mutableState.value
        val exercise = current.selectedExercise ?: return
        val currentUser = current.userName
        val validSets = current.currentSets.mapNotNull { input ->
            val reps = input.reps.toIntOrNull() ?: 0
            if (reps <= 0) return@mapNotNull null
            val enteredWeight = input.weightKg.toFloatOrNull() ?: 0f
            WorkoutSet(
                setNumber = input.setNumber,
                reps = reps,
                weightKg = enteredWeight.toKg(current.weightUnit),
                isDropSet = input.isDropSet,
                parentSetId = input.parentSetId.takeIf { it != null && it > 0 }
            )
        }
        if (validSets.isEmpty()) {
            mutableState.update { it.copy(errorMessage = "Enter at least one set with reps > 0") }
            return
        }

        viewModelScope.launch {
            mutableState.update { it.copy(isSaving = true) }
            saveWorkoutSessionUseCase(currentUser, exercise, validSets)
                .onSuccess {
                    mutableState.value = WorkoutUiState(
                        userName = currentUser,
                        loginInput = currentUser,
                        screenState = ScreenState.IDLE,
                        weightUnit = current.weightUnit,
                        successMessage = "Workout saved"
                    )
                    observeSearch("")
                }
                .onFailure { throwable ->
                    mutableState.update {
                        it.copy(isSaving = false, errorMessage = throwable.message ?: "Failed saving workout")
                    }
                }
        }
    }

    /** Opens confirm dialog before abandoning active workout. */
    fun requestAbandonWorkout() {
        mutableState.update { it.copy(showAbandonDialog = true) }
    }

    /** Closes abandon confirmation dialog. */
    fun dismissAbandonDialog() {
        mutableState.update { it.copy(showAbandonDialog = false) }
    }

    /** Drops current workout context and returns to search. */
    fun onAbandonWorkout() {
        val state = mutableState.value
        mutableState.value = WorkoutUiState(
            userName = state.userName,
            loginInput = state.userName,
            screenState = ScreenState.IDLE,
            weightUnit = state.weightUnit
        )
        observeSearch("")
    }

    /** Clears one-time success banner message after shown. */
    fun clearSuccessMessage() {
        mutableState.update { it.copy(successMessage = null) }
    }

    /** Clears one-time error banner message after shown. */
    fun clearErrorMessage() {
        mutableState.update { it.copy(errorMessage = null) }
    }

    private fun Float.toKg(unit: WeightUnit): Float =
        if (unit == WeightUnit.KG) this else this * 0.45359237f
}
