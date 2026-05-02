package com.gymlog.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Row composable for one standard set entry. */
@Composable
fun SetRow(
    setNumber: Int,
    previousReps: Int?,
    previousWeight: Float?,
    weightSuffix: String,
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
        weightSuffix = "kg",
        currentReps = "",
        currentWeight = "",
        onRepsChange = {},
        onWeightChange = {},
        onAddDropSet = {}
    )
}
