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

class Step7 : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step7)

        dbHelper = DatabaseHelper(this)

        val genderOptions = findViewById<RadioGroup>(R.id.gaolOption)  // Correcting the typo
        val continueButton = findViewById<Button>(R.id.continueButton)

        // Set up RadioGroup listener
        genderOptions.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            // Clear previous selections and reset backgrounds
            for (i in 0 until genderOptions.childCount) {
                val child = genderOptions.getChildAt(i)
                if (child is RadioButton) {
                    child.setBackgroundResource(R.drawable.radio_button_unselected) // Unselected background
                }
            }
            // Highlight the selected RadioButton
            radioButton.setBackgroundResource(R.drawable.radio_button_selected) // Selected background
            // Save the selected goal (Lose Weight or Gain Muscles)
            saveGoalToDatabase(radioButton.text.toString())
        }

        // Handle continue button click
        continueButton.setOnClickListener {
            // Ensure that a goal is selected before proceeding
            val selectedGoal = getSelectedGoal()
            if (selectedGoal.isNullOrEmpty()) {
                // Show error message if no goal is selected
                Toast.makeText(this, "Please select a goal", Toast.LENGTH_SHORT).show()
            } else {
                // Save step completion (Step 7)
                saveStepCompletionStatus(7)
                // Proceed to the next step
                NavigationHelper.navigateToNextStep(this, Step8::class.java)
            }
        }
    }

    // Save selected goal to the database
    private fun saveGoalToDatabase(goal: String) {
        val data = ContentValues().apply {
            put("goal", goal)
        }

        // Save or update the user data for this step
        dbHelper.saveOrUpdateStepData(data, "user1")
    }

    // Retrieve selected goal from the RadioGroup (no need to fetch from the database)
    private fun getSelectedGoal(): String? {
        val genderOptions = findViewById<RadioGroup>(R.id.gaolOption)
        val selectedRadioButtonId = genderOptions.checkedRadioButtonId
        return if (selectedRadioButtonId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            selectedRadioButton.text.toString()
        } else {
            null
        }
    }

    // Save step completion status in the database
    private fun saveStepCompletionStatus(step: Int) {
        val data = ContentValues().apply {
            put("stepCompleted", step)
        }

        dbHelper.saveOrUpdateStepData(data, "user1")
    }
}
