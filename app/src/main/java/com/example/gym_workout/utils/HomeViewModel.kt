package com.example.gym_workout.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gym_workout.R

class HomeViewModel : ViewModel() {
    private val _sessions = MutableLiveData<List<SessionItem>>()
    val sessions: LiveData<List<SessionItem>> get() = _sessions

    fun updateSessions(program: String) {
        _sessions.value = when (program) {
            "jog" -> listOf(
                SessionItem("Walking", "Low impact", R.drawable.walking),
                SessionItem("Running", "High impact", R.drawable.running)
            )
            "yoga" -> listOf(
                SessionItem("Yoga", "Flexibility", R.drawable.yoga_icon)
            )
            "cycling" -> listOf(
                SessionItem("Cycling", "Cardio", R.drawable.cycling)
            )
            "Loose Weight" -> listOf(
                SessionItem("Full Body (Fat Loss)", "45 mins", R.drawable.fullbody)
            )
            "MuscleGain" -> listOf(
                SessionItem("Upper Body (Muscle Gain)", "30 mins", R.drawable.upper_body),
                SessionItem("Lower Body (Muscle Gain)", "30 mins", R.drawable.lower_body)
            )
            else -> listOf(
                SessionItem("Upper Body", "Arms & Shoulders", R.drawable.upper_body),
                SessionItem("Lower Body", "Legs & Glutes", R.drawable.lower_body),
                SessionItem("Full Body", "Full body", R.drawable.fullbody)
            )
        }
    }
}
