package com.example.gym_workout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityRecognitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ActivityRecognitionReceiver", "Received an update")
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            result?.let {
                val detectedActivities = it.probableActivities
                for (activity in detectedActivities) {
                    when (activity.type) {
                        DetectedActivity.WALKING, DetectedActivity.RUNNING -> {
                            Log.d("ActivityRecognition", "User is walking or running: ${activity.confidence}")
                            // Update your step count logic here
                        }
                        else -> {
                            Log.d("ActivityRecognition", "User is not walking or running: ${activity.type}")
                        }
                    }
                }
            }
        } else {
            Log.d("ActivityRecognitionReceiver", "No activity recognition result available")
        }
    }
}
