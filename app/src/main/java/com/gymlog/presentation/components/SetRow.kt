package com.gymlog.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Row composable for one standard set entry. */
@Composable
fun SetRow(
    setNumber: Int,
    previousReps: Int?,
    previousWeight: Float?,
    currentReps: String,
    currentWeight: String,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onAddDropSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Set $setNumber", modifier = Modifier.width(52.dp))
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
        Button(onClick = onAddDropSet) { Text("+ Drop Set") }
    }
}

@Preview(showBackground = true)
@Composable
private fun SetRowPreview() {
    SetRow(
        setNumber = 1,
        previousReps = 8,
        previousWeight = 80f,
        currentReps = "",
        currentWeight = "",
        onRepsChange = {},
        onWeightChange = {},
        onAddDropSet = {}
    )
}
