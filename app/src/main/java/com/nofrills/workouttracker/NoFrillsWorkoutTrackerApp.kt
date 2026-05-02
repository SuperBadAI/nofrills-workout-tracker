package com.nofrills.workouttracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Android [Application] subclass required by Hilt so dependency injection can build a singleton graph
 * for the lifetime of the **No Frills Workout Tracker** process.
 */
@HiltAndroidApp
class NoFrillsWorkoutTrackerApp : Application()
