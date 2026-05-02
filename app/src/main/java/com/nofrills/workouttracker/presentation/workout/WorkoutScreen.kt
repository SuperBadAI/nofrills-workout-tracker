package com.nofrills.workouttracker.presentation.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.presentation.components.AddSetButton
import com.nofrills.workouttracker.presentation.components.DropSetRow
import com.nofrills.workouttracker.presentation.components.ExerciseSearchBar
import com.nofrills.workouttracker.presentation.components.SetRow

/** Single-screen gym logging UI. */
@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel = hiltViewModel()) {
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
        onCompleteWorkout = viewModel::onCompleteWorkout,
        onRequestAbandon = viewModel::requestAbandonWorkout,
        onDismissAbandon = viewModel::dismissAbandonDialog,
        onConfirmAbandon = viewModel::onAbandonWorkout,
        onSuccessShown = viewModel::clearSuccessMessage,
        onErrorShown = viewModel::clearErrorMessage
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
    onCompleteWorkout: () -> Unit,
    onRequestAbandon: () -> Unit,
    onDismissAbandon: () -> Unit,
    onConfirmAbandon: () -> Unit,
    onSuccessShown: () -> Unit,
    onErrorShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

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

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state.screenState) {
                ScreenState.LOGIN -> {
                    Text("Welcome to No Frills Workout Tracker")
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
                    Text("Logged in as ${state.userName}")
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
                    Text(
                        text = state.selectedExercise?.name.orEmpty(),
                        modifier = Modifier.clickable { onRequestAbandon() }
                    )
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

                    state.currentSets.forEachIndexed { index, set ->
                        val previous = state.previousSession?.sets?.firstOrNull {
                            it.setNumber == set.setNumber && it.isDropSet == set.isDropSet
                        }
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
                                onWeightChange = { onSetUpdated(index, set.reps, it) }
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
                                onAddDropSet = { onAddDropSet(index) }
                            )
                        }
                    }

                    AddSetButton(onClick = onAddSet)
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
            title = { Text("Abandon workout?") },
            text = { Text("Your current set inputs will be lost.") },
            confirmButton = { Button(onClick = onConfirmAbandon) { Text("Abandon") } },
            dismissButton = { Button(onClick = onDismissAbandon) { Text("Cancel") } }
        )
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
        onCompleteWorkout = {},
        onRequestAbandon = {},
        onDismissAbandon = {},
        onConfirmAbandon = {},
        onSuccessShown = {},
        onErrorShown = {}
    )
}
