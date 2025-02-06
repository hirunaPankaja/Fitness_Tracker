package com.example.gym_workout.utils

import android.content.Context
import android.content.Intent

class NavigationHelper {
    companion object {
        fun navigateToNextStep(context: Context, targetClass: Class<*>) {
            val intent = Intent(context, targetClass)
            context.startActivity(intent)
        }
    }
}
