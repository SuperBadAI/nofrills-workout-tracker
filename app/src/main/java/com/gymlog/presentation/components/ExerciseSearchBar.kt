package com.gymlog.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gymlog.domain.model.Exercise

/** Search input with results list and create-new option. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSearchBar(
    query: String,
    results: List<Exercise>,
    onQueryChange: (String) -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    onCreateExercise: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search or add exercise...") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(results, key = { it.id }) { exercise ->
                Text(
                    text = exercise.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExerciseSelected(exercise) }
                        .padding(12.dp)
                )
            }
            if (query.isNotBlank() && results.none { it.name.equals(query, ignoreCase = true) }) {
                item {
                    Text(
                        text = "+ Create '$query'",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCreateExercise(query) }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseSearchBarPreview() {
    ExerciseSearchBar(
        query = "Bench",
        results = listOf(Exercise(id = 1, name = "Bench Press")),
        onQueryChange = {},
        onExerciseSelected = {},
        onCreateExercise = {}
    )
}
