package com.nofrills.workouttracker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

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
    canRemoveSet: Boolean,
    onRemoveSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Set $setNumber",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (canRemoveSet) {
                        IconButton(onClick = onRemoveSet) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove set")
                        }
                    }
                    IconButton(onClick = onAddDropSet) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add onto set")
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = currentWeight,
                    onValueChange = onWeightChange,
                    label = { Text("Weight") },
                    placeholder = {
                        LastValuePlaceholder(previousWeight?.formatTenth() ?: "0.0")
                    },
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
                    placeholder = {
                        LastValuePlaceholder(previousReps?.toString() ?: "0")
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )
            }
        }
    }
}

/** Shows the previous workout value prominently while keeping the field empty until the user confirms it. */
@Composable
private fun LastValuePlaceholder(value: String) {
    Text(
        text = value,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

private fun Float.formatTenth(): String = String.format(Locale.US, "%.1f", this)

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
        onAddDropSet = {},
        canRemoveSet = false,
        onRemoveSet = {}
    )
}
