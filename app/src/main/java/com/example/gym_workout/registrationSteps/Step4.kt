package com.example.gym_workout.registrationSteps

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.R
import com.example.gym_workout.database.DatabaseHelper
import com.example.gym_workout.utils.NavigationHelper

class Step4 : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step4)

        dbHelper = DatabaseHelper(this)

        val weightSeekBar = findViewById<SeekBar>(R.id.weightSeekBar)
        val weightDisplay = findViewById<TextView>(R.id.weightDisplay)
        val continueButton = findViewById<Button>(R.id.continueButton)

        // Set an OnSeekBarChangeListener to update the weight display
        weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weightDisplay.text = "$progress kg"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Handle continue button click
        continueButton.setOnClickListener {
            val weight = weightSeekBar.progress
            val data = ContentValues().apply {
                put("weight", weight)
                put("stepCompleted", 4)  // Track progress
            }

            val isSaved = dbHelper.saveOrUpdateStepData(data, "user1")
            if (isSaved) {
                NavigationHelper.navigateToNextStep(this, Step5::class.java)
            } else {
                Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}