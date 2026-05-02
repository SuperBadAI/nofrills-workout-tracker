package com.gymlog.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Row composable for one drop set entry under a parent set. */
@Composable
fun DropSetRow(
    parentSetNumber: Int,
    previousReps: Int?,
    previousWeight: Float?,
    currentReps: String,
    currentWeight: String,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Set ${parentSetNumber}A")
        OutlinedTextField(
            value = currentWeight,
            onValueChange = onWeightChange,
            placeholder = { Text(previousWeight?.toString() ?: "0") },
            modifier = Modifier.weight(1f),
            suffix = { Text("kg") }
        )
        OutlinedTextField(
            value = currentReps,
            onValueChange = onRepsChange,
            placeholder = { Text(previousReps?.toString() ?: "0") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DropSetRowPreview() {
    DropSetRow(
        parentSetNumber = 2,
        previousReps = 6,
        previousWeight = 75f,
        currentReps = "",
        currentWeight = "",
        onRepsChange = {},
        onWeightChange = {}
    )
}
