package com.example.gym_workout.registrationSteps

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.R
import com.example.gym_workout.database.DatabaseHelper
import com.example.gym_workout.utils.NavigationHelper

class Step3 : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step3)

        dbHelper = DatabaseHelper(this)

        val heightPicker = findViewById<NumberPicker>(R.id.heightPicker)
        val tvConvertedHeight = findViewById<TextView>(R.id.convertedHeight)
        val cmSelector = findViewById<RadioButton>(R.id.cmSelector)
        val feetSelector = findViewById<RadioButton>(R.id.feetSelector)
        val continueButton = findViewById<Button>(R.id.continueButton)

        // Set the height picker range
        heightPicker.minValue = 100
        heightPicker.maxValue = 250
        heightPicker.value = 180

        // Set a listener for changes in the height picker
        heightPicker.setOnValueChangedListener { _, _, newVal ->
            updateHeightDisplay(newVal, cmSelector.isChecked, tvConvertedHeight)
        }

        // Handle unit selection (CM or Feet)
        cmSelector.setOnCheckedChangeListener { _, isChecked ->
            updateHeightDisplay(heightPicker.value, isChecked, tvConvertedHeight)
        }

        feetSelector.setOnCheckedChangeListener { _, isChecked ->
            updateHeightDisplay(heightPicker.value, isChecked, tvConvertedHeight)
        }

        // Handle continue button click
        continueButton.setOnClickListener {
            val selectedHeightCm = heightPicker.value
            val data = ContentValues().apply {
                put("height", selectedHeightCm)
                put("stepCompleted", 3)  // Track progress
            }

            val isSaved = dbHelper.saveOrUpdateStepData(data, "user1")
            if (isSaved) {
                NavigationHelper.navigateToNextStep(this, Step4::class.java)
            } else {
                Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateHeightDisplay(cmHeight: Int, isCm: Boolean, textView: TextView) {
        if (isCm) {
            textView.text = "$cmHeight cm"
        } else {
            val totalInches = cmHeight / 2.54
            val feet = totalInches / 12
            val inches = totalInches % 12
            textView.text = String.format("%.0f feet %.0f inches", feet, inches)
        }
    }
}