package com.example.gym_workout.registrationSteps

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.R
import com.example.gym_workout.database.DatabaseHelper
import com.example.gym_workout.utils.NavigationHelper

class Step8 : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step8)

        dbHelper = DatabaseHelper(this)

        val radioGroup = findViewById<RadioGroup>(R.id.fitnessLevelRadioGroup)
        val continueButton = findViewById<Button>(R.id.continueButton)

        continueButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedFitnessLevel = selectedRadioButton.text.toString()

                // Save selected fitness level to database
                saveFitnessLevelToDatabase(selectedFitnessLevel)

                // Show a toast message with the selected fitness level
                Toast.makeText(this, "Selected: $selectedFitnessLevel", Toast.LENGTH_SHORT).show()

                // Save step completion (Step 8)
                saveStepCompletionStatus(8)

                // Navigate to the next step (Step 9)
                NavigationHelper.navigateToNextStep(this, Step9::class.java)
            } else {
                Toast.makeText(this, "Please select a fitness level", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save selected fitness level to the database
    private fun saveFitnessLevelToDatabase(fitnessLevel: String) {
        val contentValues = ContentValues().apply {
            put("fitnessLevel", fitnessLevel)
        }
        // Save or update the user data for this step
        dbHelper.saveOrUpdateStepData(contentValues, "user1")
    }

    // Save step completion status in the database
    private fun saveStepCompletionStatus(step: Int) {
        val contentValues = ContentValues().apply {
            put("stepCompleted", step)
        }
        // Save step completion status
        dbHelper.saveOrUpdateStepData(contentValues, "user1")
    }
}
