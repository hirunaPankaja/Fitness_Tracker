package com.example.gym_workout.registrationSteps

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.R
import com.example.gym_workout.database.DatabaseHelper

import com.example.gym_workout.utils.NavigationHelper


class Step2 : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step2)

        dbHelper = DatabaseHelper(this)

        val genderOptions = findViewById<RadioGroup>(R.id.genderOptions)
        val continueButton = findViewById<Button>(R.id.continueButton)

        // Set up RadioGroup listener
        genderOptions.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            selectedGender = radioButton.text.toString()
            Log.d("Step2", "Selected gender: $selectedGender")
        }

        // Handle continue button click
        continueButton.setOnClickListener {
            if (selectedGender.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
            } else {
                val data = ContentValues().apply {
                    put("gender", selectedGender)
                    put("stepCompleted", 2)  // Track progress
                }

                val isSaved = dbHelper.saveOrUpdateStepData(data, "user1")
                if (isSaved) {
                    NavigationHelper.navigateToNextStep(this, Step3::class.java)
                } else {
                    Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}