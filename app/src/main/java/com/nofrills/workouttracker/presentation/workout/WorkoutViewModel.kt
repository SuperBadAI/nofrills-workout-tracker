package com.nofrills.workouttracker.presentation.workout

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.model.WorkoutSet
import com.nofrills.workouttracker.domain.usecase.ExportToCsvUseCase
import com.nofrills.workouttracker.domain.usecase.GetLastSessionForExerciseUseCase
import com.nofrills.workouttracker.domain.usecase.GetOrCreateExerciseUseCase
import com.nofrills.workouttracker.domain.usecase.ObserveUserNamesWithDataUseCase
import com.nofrills.workouttracker.domain.usecase.RenameExerciseUseCase
import com.nofrills.workouttracker.domain.usecase.SaveWorkoutSessionUseCase
import com.nofrills.workouttracker.domain.usecase.SearchExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

/** ViewModel that coordinates login, search, set edits, and save flow for **No Frills Workout Tracker**. */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val searchExercisesUseCase: SearchExercisesUseCase,
    private val getOrCreateExerciseUseCase: GetOrCreateExerciseUseCase,
    private val getLastSessionForExerciseUseCase: GetLastSessionForExerciseUseCase,
    private val saveWorkoutSessionUseCase: SaveWorkoutSessionUseCase,
    private val exportToCsvUseCase: ExportToCsvUseCase,
    private val renameExerciseUseCase: RenameExerciseUseCase,
    observeUserNamesWithDataUseCase: ObserveUserNamesWithDataUseCase
) : ViewModel() {

    private val mutableState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = mutableState.asStateFlow()

    private val shareCsvUriEventsMutable = MutableSharedFlow<Uri>(extraBufferCapacity = 1)
    val shareCsvUriEvents: SharedFlow<Uri> = shareCsvUriEventsMutable.asSharedFlow()

    private var searchJob: Job? = null

    init {
        observeUserNamesWithDataUseCase()
            .onEach { names -> mutableState.update { it.copy(userNamesWithData = names) } }
            .launchIn(viewModelScope)
    }

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
        mutableState.update { state ->
            if (state.weightUnit == unit) {
                state
            } else {
                state.copy(
                    weightUnit = unit,
                    currentSets = state.currentSets.map { row ->
                        row.copy(weightKg = row.weightKg.convertWeightInput(state.weightUnit, unit))
                    }
                )
            }
        }
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

    /** Selects an exercise and pre-fills current rows from the latest matching session for this user. */
    fun onExerciseSelected(exercise: Exercise) {
        val currentUser = mutableState.value.userName
        mutableState.update {
            it.copy(
                screenState = ScreenState.EXERCISE_SELECTED,
                selectedExercise = exercise,
                exerciseNameDraft = exercise.name,
                searchQuery = exercise.name,
                currentSets = listOf(MutableSetInput(setNumber = 1)),
                previousSession = null,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            getLastSessionForExerciseUseCase(exercise.id, currentUser)
                .onSuccess { session ->
                    mutableState.update { state ->
                        if (state.selectedExercise?.id != exercise.id) {
                            state
                        } else {
                            state.copy(
                                previousSession = session?.copy(exercise = exercise),
                                currentSets = buildInitialSetInputs(
                                    previousSets = session?.sets,
                                    weightUnit = state.weightUnit
                                )
                            )
                        }
                    }
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

    /** Updates the in-progress exercise name field (saved with [onSaveExerciseName]). */
    fun onExerciseNameDraftChanged(value: String) {
        mutableState.update { it.copy(exerciseNameDraft = value) }
    }

    /** Persists [exerciseNameDraft] as the canonical exercise name for this lift. */
    fun onSaveExerciseName() {
        val exercise = mutableState.value.selectedExercise ?: return
        val draft = mutableState.value.exerciseNameDraft.trim()
        if (draft.isBlank()) {
            mutableState.update { it.copy(errorMessage = "Exercise name cannot be empty") }
            return
        }
        if (draft.equals(exercise.name, ignoreCase = true)) {
            mutableState.update { it.copy(exerciseNameDraft = exercise.name) }
            return
        }
        viewModelScope.launch {
            mutableState.update { it.copy(isRenamingExercise = true, errorMessage = null) }
            renameExerciseUseCase(exercise.id, draft)
                .onSuccess { updated ->
                    mutableState.update { s ->
                        s.copy(
                            isRenamingExercise = false,
                            selectedExercise = updated,
                            exerciseNameDraft = updated.name,
                            searchQuery = updated.name,
                            previousSession = s.previousSession?.copy(exercise = updated),
                            successMessage = "Exercise name updated"
                        )
                    }
                }
                .onFailure { throwable ->
                    mutableState.update {
                        it.copy(
                            isRenamingExercise = false,
                            errorMessage = throwable.message ?: "Could not rename exercise"
                        )
                    }
                }
        }
    }

    /** Removes one set row (and any drop sets for that set when removing a main set); renumbers remaining rows. */
    fun onRemoveSet(index: Int) {
        mutableState.update { state ->
            val list = state.currentSets
            val target = list.getOrNull(index) ?: return@update state
            val filtered = if (target.isDropSet) {
                list.filterIndexed { i, _ -> i != index }
            } else {
                val n = target.setNumber
                list.filterIndexed { i, row ->
                    when {
                        i == index -> false
                        row.isDropSet && row.setNumber == n -> false
                        else -> true
                    }
                }
            }
            var renumbered = renumberMutableSets(filtered)
            if (renumbered.isEmpty()) {
                renumbered = listOf(MutableSetInput(setNumber = 1))
            }
            state.copy(currentSets = renumbered)
        }
    }

    private fun renumberMutableSets(rows: List<MutableSetInput>): List<MutableSetInput> {
        var main = 0
        return rows.map { row ->
            if (!row.isDropSet) {
                main++
                row.copy(setNumber = main)
            } else {
                row.copy(setNumber = main)
            }
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
                weightKg = enteredWeight.toKg(current.weightUnit).roundToTenth(),
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
                    mutableState.update { old ->
                        WorkoutUiState(
                            userName = currentUser,
                            loginInput = currentUser,
                            screenState = ScreenState.IDLE,
                            weightUnit = current.weightUnit,
                            successMessage = "Workout saved",
                            userNamesWithData = old.userNamesWithData
                        )
                    }
                    observeSearch("")
                }
                .onFailure { throwable ->
                    mutableState.update {
                        it.copy(isSaving = false, errorMessage = throwable.message ?: "Failed saving workout")
                    }
                }
        }
    }

    /**
     * Returns to exercise search when the user wants a different lift. If they already typed weight or reps, we ask
     * for confirmation so accidental taps do not discard in-progress input.
     */
    fun onBackFromExercise() {
        val state = mutableState.value
        if (state.currentSets.any { it.reps.isNotBlank() || it.weightKg.isNotBlank() }) {
            requestAbandonWorkout()
        } else {
            onAbandonWorkout()
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
            weightUnit = state.weightUnit,
            showAbandonDialog = false,
            userNamesWithData = state.userNamesWithData
        )
        observeSearch("")
    }

    /** Opens the dialog to pick which user’s CSV to share. */
    fun onShareCsvClicked() {
        val s = mutableState.value
        val default = when {
            s.userName.isNotBlank() && s.userName in s.userNamesWithData -> s.userName
            s.userNamesWithData.isNotEmpty() -> s.userNamesWithData.first()
            else -> ""
        }
        mutableState.update {
            it.copy(showShareCsvDialog = true, shareCsvSelectedUser = default)
        }
    }

    fun onShareCsvDialogDismiss() {
        mutableState.update { it.copy(showShareCsvDialog = false) }
    }

    fun onShareCsvUserSelected(user: String) {
        mutableState.update { it.copy(shareCsvSelectedUser = user) }
    }

    /** Builds a CSV for the selected user and emits its content Uri for the activity share sheet. */
    fun onShareCsvConfirmed() {
        val user = mutableState.value.shareCsvSelectedUser
        if (user.isBlank()) {
            mutableState.update { it.copy(errorMessage = "Pick a user to export") }
            return
        }
        mutableState.update { it.copy(showShareCsvDialog = false, isExportingCsv = true) }
        viewModelScope.launch {
            exportToCsvUseCase(user)
                .onSuccess { uri ->
                    mutableState.update { it.copy(isExportingCsv = false) }
                    if (uri != null) {
                        shareCsvUriEventsMutable.emit(uri)
                    } else {
                        mutableState.update { it.copy(errorMessage = "Could not create CSV file") }
                    }
                }
                .onFailure { throwable ->
                    mutableState.update {
                        it.copy(
                            isExportingCsv = false,
                            errorMessage = throwable.message ?: "Export failed"
                        )
                    }
                }
        }
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

    private fun Float.toDisplayWeight(unit: WeightUnit): Float =
        if (unit == WeightUnit.KG) this else this * 2.2046226f

    private fun Float.roundToTenth(): Float = (this * 10f).roundToInt() / 10f

    private fun Float.formatWeightInput(): String = String.format(Locale.US, "%.1f", roundToTenth())

    private fun String.convertWeightInput(from: WeightUnit, to: WeightUnit): String {
        val typedWeight = toFloatOrNull() ?: return this
        return typedWeight
            .toKg(from)
            .toDisplayWeight(to)
            .formatWeightInput()
    }

    private fun buildInitialSetInputs(
        previousSets: List<WorkoutSet>?,
        weightUnit: WeightUnit
    ): List<MutableSetInput> {
        val sortedPrevious = previousSets
            ?.sortedWith(compareBy<WorkoutSet> { it.setNumber }.thenBy { it.isDropSet })
            .orEmpty()

        if (sortedPrevious.isEmpty()) return listOf(MutableSetInput(setNumber = 1))

        return sortedPrevious.map { previous ->
            MutableSetInput(
                setNumber = previous.setNumber,
                reps = previous.reps.toString(),
                weightKg = previous.weightKg.toDisplayWeight(weightUnit).formatWeightInput(),
                isDropSet = previous.isDropSet,
                parentSetId = previous.parentSetId
            )
        }
    }
}
