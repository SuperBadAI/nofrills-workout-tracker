package com.gymlog.presentation.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier

/** CTA button used to append a new set row. */
@Composable
fun AddSetButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier) {
        Text("+ Add Set")
    }
}

@Preview
@Composable
private fun AddSetButtonPreview() {
    AddSetButton(onClick = {})
}
