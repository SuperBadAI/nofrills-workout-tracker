package com.nofrills.workouttracker.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nofrills.workouttracker.domain.model.Exercise
import com.nofrills.workouttracker.domain.model.WorkoutSession
import com.nofrills.workouttracker.domain.model.WorkoutSet
import com.nofrills.workouttracker.presentation.workout.WeightUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Read-only summary of the last completed session for the same exercise so the athlete can see every set, weight, and
 * rep target before logging new work.
 */
@Composable
fun LastSessionSummary(
    session: WorkoutSession?,
    weightUnit: WeightUnit,
    modifier: Modifier = Modifier
) {
    if (session == null || session.sets.isEmpty()) return
    val hintAlpha = 0.45f
    val dateLabel = formatCompletedDate(session.completedAt)
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Last time ($dateLabel)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .alpha(hintAlpha)
                .padding(bottom = 4.dp)
        )
        session.sets.sortedWith(compareBy({ it.setNumber }, { it.isDropSet })).forEach { set ->
            Text(
                text = formatSetLine(set, weightUnit),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .alpha(hintAlpha)
                    .padding(vertical = 2.dp)
            )
        }
    }
}

private fun formatCompletedDate(completedAtMillis: Long): String {
    val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return fmt.format(Date(completedAtMillis))
}

private fun formatSetLine(set: WorkoutSet, weightUnit: WeightUnit): String {
    val label = if (set.isDropSet) "Set ${set.setNumber}A" else "Set ${set.setNumber}"
    val w = displayWeight(set.weightKg, weightUnit)
    val unit = weightUnit.name.lowercase(Locale.US)
    return "$label: $w $unit × ${set.reps} reps"
}

private fun displayWeight(weightKg: Float, weightUnit: WeightUnit): String {
    val v = if (weightUnit == WeightUnit.KG) weightKg else weightKg * 2.2046226f
    return String.format(Locale.US, "%.1f", v)
}

@Preview(showBackground = true)
@Composable
private fun LastSessionSummaryPreview() {
    LastSessionSummary(
        session = WorkoutSession(
            userName = "u",
            exercise = Exercise(id = 1, name = "Bench"),
            sets = listOf(
                WorkoutSet(setNumber = 1, reps = 10, weightKg = 80f),
                WorkoutSet(setNumber = 2, reps = 8, weightKg = 82.5f),
                WorkoutSet(setNumber = 2, reps = 6, weightKg = 75f, isDropSet = true)
            ),
            completedAt = System.currentTimeMillis()
        ),
        weightUnit = WeightUnit.KG
    )
}
