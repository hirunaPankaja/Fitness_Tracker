package com.example.gym_workout

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

        switchNotifications.isChecked = true
        checkboxSync.isChecked = false
        switchDarkMode.isChecked = isDarkMode

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
    }
}
