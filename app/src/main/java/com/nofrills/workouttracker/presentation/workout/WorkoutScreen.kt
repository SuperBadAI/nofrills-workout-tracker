package com.nofrills.workouttracker.presentation.workout

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.presentation.components.AddSetButton
import com.nofrills.workouttracker.presentation.components.DropSetRow
import com.nofrills.workouttracker.presentation.components.ExerciseSearchBar
import com.nofrills.workouttracker.presentation.components.SetRow

/** Single-screen workout logging UI for **No Frills Workout Tracker**. */
@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel(),
    onShareCsvUri: (Uri) -> Unit = { _ -> }
) {
    LaunchedEffect(viewModel) {
        viewModel.shareCsvUriEvents.collect { uri -> onShareCsvUri(uri) }
    }
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    WorkoutScreenContent(
        state = state,
        onLoginInputChanged = viewModel::onLoginInputChanged,
        onLoginConfirmed = viewModel::onLoginConfirmed,
        onWeightUnitChanged = viewModel::onWeightUnitChanged,
        onQueryChange = viewModel::onSearchQueryChanged,
        onExerciseSelected = viewModel::onExerciseSelected,
        onCreateExercise = viewModel::onExerciseCreated,
        onSetUpdated = viewModel::onSetUpdated,
        onAddDropSet = viewModel::onAddDropSet,
        onAddSet = viewModel::onAddSet,
        onRemoveSet = viewModel::onRemoveSet,
        onExerciseNameDraftChanged = viewModel::onExerciseNameDraftChanged,
        onSaveExerciseName = viewModel::onSaveExerciseName,
        onCompleteWorkout = viewModel::onCompleteWorkout,
        onBackFromExercise = viewModel::onBackFromExercise,
        onDismissAbandon = viewModel::dismissAbandonDialog,
        onConfirmAbandon = viewModel::onAbandonWorkout,
        onSuccessShown = viewModel::clearSuccessMessage,
        onErrorShown = viewModel::clearErrorMessage,
        onShareCsvClicked = viewModel::onShareCsvClicked,
        onShareCsvDialogDismiss = viewModel::onShareCsvDialogDismiss,
        onShareCsvUserSelected = viewModel::onShareCsvUserSelected,
        onShareCsvConfirmed = viewModel::onShareCsvConfirmed
    )
}

/** Stateless content renderer for workout UI. */
@Composable
fun WorkoutScreenContent(
    state: WorkoutUiState,
    onLoginInputChanged: (String) -> Unit,
    onLoginConfirmed: () -> Unit,
    onWeightUnitChanged: (WeightUnit) -> Unit,
    onQueryChange: (String) -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    onCreateExercise: (String) -> Unit,
    onSetUpdated: (Int, String, String) -> Unit,
    onAddDropSet: (Int) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onExerciseNameDraftChanged: (String) -> Unit,
    onSaveExerciseName: () -> Unit,
    onCompleteWorkout: () -> Unit,
    onBackFromExercise: () -> Unit,
    onDismissAbandon: () -> Unit,
    onConfirmAbandon: () -> Unit,
    onSuccessShown: () -> Unit,
    onErrorShown: () -> Unit,
    onShareCsvClicked: () -> Unit,
    onShareCsvDialogDismiss: () -> Unit,
    onShareCsvUserSelected: (String) -> Unit,
    onShareCsvConfirmed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val workoutScrollState = rememberScrollState()

    LaunchedEffect(state.successMessage, state.errorMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onSuccessShown()
        }
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onErrorShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .then(
                    if (state.screenState == ScreenState.EXERCISE_SELECTED) {
                        Modifier.verticalScroll(workoutScrollState)
                    } else {
                        Modifier
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state.screenState) {
                ScreenState.LOGIN -> {
                    Text(
                        text = "Welcome to No Frills Workout Tracker",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = state.loginInput,
                        onValueChange = onLoginInputChanged,
                        label = { Text("Username") },
                        placeholder = { Text("Enter your name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(onClick = onLoginConfirmed, modifier = Modifier.fillMaxWidth()) {
                        Text("Continue")
                    }
                }

                ScreenState.IDLE -> {
                    LoggedInHeader(userName = state.userName)
                    OutlinedButton(
                        onClick = onShareCsvClicked,
                        enabled = !state.isExportingCsv,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.isExportingCsv) "Preparing CSV…" else "Share workout CSV…")
                    }
                    ExerciseSearchBar(
                        query = state.searchQuery,
                        results = state.searchResults,
                        onQueryChange = onQueryChange,
                        onExerciseSelected = onExerciseSelected,
                        onCreateExercise = onCreateExercise,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ScreenState.EXERCISE_SELECTED -> {
                    LoggedInHeader(userName = state.userName)
                    TextButton(onClick = onBackFromExercise, modifier = Modifier.fillMaxWidth()) {
                        Text("← Change exercise")
                    }
                    OutlinedButton(
                        onClick = onShareCsvClicked,
                        enabled = !state.isExportingCsv,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.isExportingCsv) "Preparing CSV…" else "Share workout CSV…")
                    }
                    val selected = state.selectedExercise
                    val nameDirty = selected != null &&
                        state.exerciseNameDraft.trim().isNotBlank() &&
                        !state.exerciseNameDraft.trim().equals(selected.name, ignoreCase = true)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = state.exerciseNameDraft,
                            onValueChange = onExerciseNameDraftChanged,
                            label = { Text("Exercise") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !state.isRenamingExercise
                        )
                        TextButton(
                            onClick = onSaveExerciseName,
                            enabled = nameDirty && !state.isRenamingExercise
                        ) {
                            Text(if (state.isRenamingExercise) "…" else "Save name")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = state.weightUnit == WeightUnit.KG,
                            onClick = { onWeightUnitChanged(WeightUnit.KG) },
                            label = { Text("KG") }
                        )
                        FilterChip(
                            selected = state.weightUnit == WeightUnit.LBS,
                            onClick = { onWeightUnitChanged(WeightUnit.LBS) },
                            label = { Text("LBS") }
                        )
                    }

                    val canRemoveAnySet = state.currentSets.size > 1
                    val previousSets = state.previousSession
                        ?.sets
                        ?.sortedWith(compareBy({ it.setNumber }, { it.isDropSet }))
                        .orEmpty()
                    state.currentSets.forEachIndexed { index, set ->
                        val previous = previousSets.getOrNull(index)
                            ?.takeIf { it.setNumber == set.setNumber && it.isDropSet == set.isDropSet }
                        val previousWeightInUnit = previous?.weightKg?.let { kg ->
                            if (state.weightUnit == WeightUnit.KG) kg else kg * 2.2046226f
                        }
                        if (set.isDropSet) {
                            DropSetRow(
                                parentSetNumber = set.setNumber,
                                previousReps = previous?.reps,
                                previousWeight = previousWeightInUnit,
                                weightSuffix = state.weightUnit.name.lowercase(),
                                currentReps = set.reps,
                                currentWeight = set.weightKg,
                                onRepsChange = { onSetUpdated(index, it, set.weightKg) },
                                onWeightChange = { onSetUpdated(index, set.reps, it) },
                                canRemoveSet = canRemoveAnySet,
                                onRemoveSet = { onRemoveSet(index) }
                            )
                        } else {
                            SetRow(
                                setNumber = set.setNumber,
                                previousReps = previous?.reps,
                                previousWeight = previousWeightInUnit,
                                weightSuffix = state.weightUnit.name.lowercase(),
                                currentReps = set.reps,
                                currentWeight = set.weightKg,
                                onRepsChange = { onSetUpdated(index, it, set.weightKg) },
                                onWeightChange = { onSetUpdated(index, set.reps, it) },
                                onAddDropSet = { onAddDropSet(index) },
                                canRemoveSet = canRemoveAnySet,
                                onRemoveSet = { onRemoveSet(index) }
                            )
                        }
                    }

                    AddSetButton(onClick = onAddSet, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    val canComplete = state.currentSets.any { (it.reps.toIntOrNull() ?: 0) > 0 }
                    Button(
                        onClick = onCompleteWorkout,
                        enabled = canComplete && !state.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.isSaving) "Saving..." else "Complete")
                    }
                }
            }
        }
    }

    if (state.showAbandonDialog) {
        AlertDialog(
            onDismissRequest = onDismissAbandon,
            title = { Text("Leave this exercise?") },
            text = { Text("You have entered weight or reps. Going back will discard those values.") },
            confirmButton = { Button(onClick = onConfirmAbandon) { Text("Discard and go back") } },
            dismissButton = { Button(onClick = onDismissAbandon) { Text("Cancel") } }
        )
    }

    if (state.showShareCsvDialog) {
        AlertDialog(
            onDismissRequest = onShareCsvDialogDismiss,
            title = { Text("Share workout CSV") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose a user. The file includes only that person’s saved workouts.")
                    if (state.userNamesWithData.isEmpty()) {
                        Text("No saved workouts yet.")
                    } else {
                        state.userNamesWithData.forEach { name ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = name == state.shareCsvSelectedUser,
                                        onClick = { onShareCsvUserSelected(name) }
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = name == state.shareCsvSelectedUser,
                                    onClick = null
                                )
                                Text(name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onShareCsvConfirmed,
                    enabled = state.userNamesWithData.isNotEmpty() && state.shareCsvSelectedUser.isNotBlank()
                ) {
                    Text("Share")
                }
            },
            dismissButton = { TextButton(onClick = onShareCsvDialogDismiss) { Text("Cancel") } }
        )
    }
}

/** Standard signed-in heading shown above every post-login screen. */
@Composable
private fun LoggedInHeader(userName: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "No Frills Workout Tracker",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Logged in as $userName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutScreenContentPreview() {
    WorkoutScreenContent(
        state = WorkoutUiState(
            screenState = ScreenState.EXERCISE_SELECTED,
            userName = "Adam",
            selectedExercise = Exercise(id = 1, name = "Bench Press"),
            exerciseNameDraft = "Bench Press",
            currentSets = listOf(
                MutableSetInput(setNumber = 1),
                MutableSetInput(setNumber = 1, isDropSet = true)
            )
        ),
        onLoginInputChanged = {},
        onLoginConfirmed = {},
        onWeightUnitChanged = {},
        onQueryChange = {},
        onExerciseSelected = {},
        onCreateExercise = {},
        onSetUpdated = { _, _, _ -> },
        onAddDropSet = {},
        onAddSet = {},
        onRemoveSet = {},
        onExerciseNameDraftChanged = {},
        onSaveExerciseName = {},
        onCompleteWorkout = {},
        onBackFromExercise = {},
        onDismissAbandon = {},
        onConfirmAbandon = {},
        onSuccessShown = {},
        onErrorShown = {},
        onShareCsvClicked = {},
        onShareCsvDialogDismiss = {},
        onShareCsvUserSelected = {},
        onShareCsvConfirmed = {}
    )
}
