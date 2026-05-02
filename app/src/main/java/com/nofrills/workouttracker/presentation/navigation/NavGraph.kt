package com.nofrills.workouttracker.presentation.navigation

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nofrills.workouttracker.presentation.workout.WorkoutScreen

/** Navigation graph with a single workout destination. */
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "workout") {
        composable("workout") {
            WorkoutScreen(
                onShareCsvUri = { uri: Uri ->
                    val activity = context as? Activity ?: return@WorkoutScreen
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        clipData = ClipData.newUri(activity.contentResolver, "Workout CSV", uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(send, "Share workout CSV").apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    activity.startActivity(chooser)
                }
            )
        }
    }
}
