package com.nofrills.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.nofrills.workouttracker.presentation.navigation.NavGraph
import com.nofrills.workouttracker.ui.theme.NoFrillsWorkoutTrackerTheme

/** Single-activity host for the **No Frills Workout Tracker** Compose UI. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoFrillsWorkoutTrackerTheme {
                NavGraph()
            }
        }
    }
}
