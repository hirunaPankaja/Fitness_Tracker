package com.example.gym_workout

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class Setting : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.setting)

        val switchNotifications: SwitchCompat = findViewById(R.id.switch_notifications)
        val checkboxSync: CheckBox = findViewById(R.id.checkbox_sync)
        val switchDarkMode: SwitchCompat = findViewById(R.id.switch_dark_mode)
        val switchEnableWidget: SwitchCompat = findViewById(R.id.enable_widget)

        switchNotifications.isChecked = true
        checkboxSync.isChecked = false
        switchDarkMode.isChecked = isDarkMode
        switchEnableWidget.isChecked = sharedPreferences.getBoolean("widget_enabled", false)

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Handle notification toggle state
        }

        checkboxSync.setOnCheckedChangeListener { _, isChecked ->
            // Handle sync data checkbox state
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            recreate()
        }

        switchEnableWidget.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("widget_enabled", isChecked)
            editor.apply()

            if (isChecked) {
                // Enable the widget by sending a broadcast
                sendBroadcastToWidget()
            } else {
                // Disable the widget by sending a broadcast
                sendBroadcastToWidget()
            }
        }
    }

    private fun sendBroadcastToWidget() {
        val intent = Intent(this, fitness_widget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, fitness_widget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}
