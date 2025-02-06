package com.example.gym_workout

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.database.DatabaseHelper

class SetPassword : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.set_password)

        dbHelper = DatabaseHelper(this)

        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)
        val saveButton = findViewById<Button>(R.id.savePasswordButton)

        saveButton.setOnClickListener {
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading screen
            showLoadingScreen()

            // Save the password to the database
            savePasswordToDatabase(password)
        }
    }

    private fun showLoadingScreen() {
        val loadingView = findViewById<View>(R.id.loadingScreen)
        loadingView.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        val loadingView = findViewById<View>(R.id.loadingScreen)
        loadingView.visibility = View.GONE
    }

    private fun savePasswordToDatabase(password: String) {
        val dbHelper = DatabaseHelper(this)

        val data = ContentValues().apply {
            put("password", password)
            put("stepCompleted", 12) // Track progress to step 12 (Set Password)
        }

        val isSaved = dbHelper.saveOrUpdateStepData(data, "user1")  // Use "user1" for consistency

        hideLoadingScreen()

        if (isSaved) {
            Toast.makeText(this, "Password set successfully!", Toast.LENGTH_SHORT).show()

            // Navigate to the next screen (e.g., MainActivity1)
            val intent = Intent(this, MainActivity1::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Failed to set password", Toast.LENGTH_SHORT).show()
        }
    }
}
