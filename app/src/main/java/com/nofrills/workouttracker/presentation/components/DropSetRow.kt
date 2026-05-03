package com.nofrills.workouttracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Row composable for one drop set entry under a parent set. */
@Composable
fun DropSetRow(
    parentSetNumber: Int,
    previousReps: Int?,
    previousWeight: Float?,
    weightSuffix: String,
    currentReps: String,
    currentWeight: String,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    canRemoveSet: Boolean,
    onRemoveSet: () -> Unit,
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
            label = { Text("Weight") },
            placeholder = { Text(previousWeight?.toString() ?: "0") },
            modifier = Modifier.weight(1f),
            suffix = { Text(weightSuffix) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        OutlinedTextField(
            value = currentReps,
            onValueChange = onRepsChange,
            label = { Text("Reps") },
            placeholder = { Text(previousReps?.toString() ?: "0") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )
        if (canRemoveSet) {
            IconButton(onClick = onRemoveSet) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove drop set")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DropSetRowPreview() {
    DropSetRow(
        parentSetNumber = 2,
        previousReps = 6,
        previousWeight = 75f,
        weightSuffix = "kg",
        currentReps = "",
        currentWeight = "",
        onRepsChange = {},
        onWeightChange = {},
        canRemoveSet = true,
        onRemoveSet = {}
    )
}
