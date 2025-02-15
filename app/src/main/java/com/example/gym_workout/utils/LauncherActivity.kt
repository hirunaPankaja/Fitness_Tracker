package com.example.gym_workout.utils

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.MainActivity1
import com.example.gym_workout.database.DatabaseHelper
import com.example.gym_workout.step1

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val databaseHelper = DatabaseHelper(this)

        val isDatabaseCreated = databaseHelper.isDatabaseCreated()
        val isRegistered = if (isDatabaseCreated) {
            databaseHelper.isUserRegistered()
        } else {
            false
        }

        val intent = if (isRegistered) {
            Intent(this, MainActivity1::class.java)
        } else {
            Intent(this, step1::class.java)
        }

        startActivity(intent)
        finish()
    }
}
