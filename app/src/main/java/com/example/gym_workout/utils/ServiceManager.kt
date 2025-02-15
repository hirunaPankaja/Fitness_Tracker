package com.example.gym_workout

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi

class ServiceManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("service_prefs", Context.MODE_PRIVATE)
    private val handler = Handler(Looper.getMainLooper())
    private var blinkRunnable: Runnable? = null

    companion object {
        const val PREF_STEPS_ENABLED = "steps_service_enabled"
        const val PREF_LOCATION_ENABLED = "location_service_enabled"
    }

    fun isStepsServiceEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_STEPS_ENABLED, false)
    }

    fun isLocationServiceEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_LOCATION_ENABLED, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleStepsService(enabled: Boolean, stepsIndicator: View) {
        sharedPreferences.edit().putBoolean(PREF_STEPS_ENABLED, enabled).apply()
        updateServiceState()
        updateIndicator(stepsIndicator, enabled)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleLocationService(enabled: Boolean, locationIndicator: View) {
        sharedPreferences.edit().putBoolean(PREF_LOCATION_ENABLED, enabled).apply()
        updateServiceState()
        updateIndicator(locationIndicator, enabled)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateServiceState() {
        val serviceIntent = Intent(context, LocationAndStepService::class.java)

        if (isStepsServiceEnabled() || isLocationServiceEnabled()) {
            context.startForegroundService(serviceIntent)
        } else {
            context.stopService(serviceIntent)
        }
    }

    private fun updateIndicator(indicator: View, enabled: Boolean) {
        if (enabled) {
            startBlinking(indicator)
        } else {
            stopBlinking(indicator)
            indicator.setBackgroundResource(R.drawable.circle_red)
        }
    }

    private fun startBlinking(indicator: View) {
        var isGreen = true
        blinkRunnable = object : Runnable {
            override fun run() {
                indicator.setBackgroundResource(
                    if (isGreen) R.drawable.circle_green else R.drawable.circle_off
                )
                isGreen = !isGreen
                handler.postDelayed(this, 500) // Blink every 500ms
            }
        }
        handler.post(blinkRunnable!!)
    }

    private fun stopBlinking(indicator: View) {
        blinkRunnable?.let { handler.removeCallbacks(it) }
        blinkRunnable = null
    }

    fun restoreState(stepsIndicator: View, locationIndicator: View) {
        if (isStepsServiceEnabled()) {
            updateIndicator(stepsIndicator, true)
        }
        if (isLocationServiceEnabled()) {
            updateIndicator(locationIndicator, true)
        }
    }
}