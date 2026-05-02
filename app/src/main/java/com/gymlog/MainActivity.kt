package com.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.gymlog.presentation.navigation.NavGraph
import com.gymlog.ui.theme.GymLogTheme

/** Hosts the single-screen GymLog compose app. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymLogTheme {
                NavGraph()
            }
        }
    }
}
