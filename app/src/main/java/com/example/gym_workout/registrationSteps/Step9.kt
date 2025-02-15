package com.example.gym_workout.registrationSteps

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.MainActivity1
import com.example.gym_workout.R
import com.example.gym_workout.database.DatabaseHelper
import com.example.gym_workout.utils.NavigationHelper

class Step9 : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step9)

        dbHelper = DatabaseHelper(this)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val continueButton = findViewById<Button>(R.id.continueButton)

        continueButton.setOnClickListener {
            val selectedOptionId = radioGroup.checkedRadioButtonId

            if (selectedOptionId != -1) {
                val selectedOption = findViewById<RadioButton>(selectedOptionId).text.toString()
                val data = ContentValues().apply {
                    put("dailyWorkingTime", selectedOption)
                    put("stepCompleted", 9)  // Track progress
                }

                val isSaved = dbHelper.saveOrUpdateStepData(data, "user1")
                if (isSaved) {
                    NavigationHelper.navigateToNextStep(this, MainActivity1::class.java)
                } else {
                    Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select how much you walk daily", Toast.LENGTH_SHORT).show()
            }
        }
    }
}