package com.nofrills.workouttracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nofrills.workouttracker.presentation.workout.WorkoutScreen

/** Navigation graph with a single workout destination. */
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "workout") {
        composable("workout") {
            WorkoutScreen()
        }
    }
}
