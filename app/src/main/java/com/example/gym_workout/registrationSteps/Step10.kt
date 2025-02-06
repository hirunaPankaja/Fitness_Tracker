package com.example.gym_workout.registrationSteps

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.R
import com.example.gym_workout.database.DatabaseHelper
import com.example.gym_workout.utils.NavigationHelper
import java.util.*

import android.content.SharedPreferences
import java.util.UUID

class Step10 : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step10)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("FitnessAppPrefs", MODE_PRIVATE)

        // Get or create userId
        userId = sharedPreferences.getString("userId", null) ?: UUID.randomUUID().toString()

        // Save userId to SharedPreferences if new
        sharedPreferences.edit().putString("userId", userId).apply()

        val firstNameEditText = findViewById<EditText>(R.id.etFirstName)
        val lastNameEditText = findViewById<EditText>(R.id.etLastName)
        val yearSpinner = findViewById<Spinner>(R.id.spinnerYear)
        val monthSpinner = findViewById<Spinner>(R.id.spinnerMonth)
        val daySpinner = findViewById<Spinner>(R.id.spinnerDay)
        val resultTextView = findViewById<TextView>(R.id.tvResult)
        val continueButton = findViewById<Button>(R.id.continueButton)

        // Populate spinners with data
        populateSpinners(yearSpinner, monthSpinner, daySpinner)

        // Update age when spinners are changed
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val year = yearSpinner.selectedItem?.toString()?.toIntOrNull()
                val month = monthSpinner.selectedItemPosition
                val day = daySpinner.selectedItem?.toString()?.toIntOrNull()

                if (year != null && month != -1 && day != null) {
                    val age = calculateAge(year, month, day)
                    resultTextView.text = "Your age is $age years."
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        yearSpinner.onItemSelectedListener = spinnerListener
        monthSpinner.onItemSelectedListener = spinnerListener
        daySpinner.onItemSelectedListener = spinnerListener

        continueButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val year = yearSpinner.selectedItem?.toString()?.toIntOrNull()
            val month = monthSpinner.selectedItemPosition
            val day = daySpinner.selectedItem?.toString()?.toIntOrNull()

            if (firstName.isEmpty() || lastName.isEmpty() || year == null || month == -1 || day == null) {
                Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = ContentValues().apply {
                put("userId", userId)  // Ensure userId is included
                put("firstName", firstName)
                put("lastName", lastName)
                put("dateOfBirth", "$year-${month + 1}-$day")  // YYYY-MM-DD format
                put("stepCompleted", 10)  // Track progress
            }

            val isSaved = databaseHelper.saveOrUpdateStepData(data, "user1")
            if (isSaved) {
                NavigationHelper.navigateToNextStep(this, Step2::class.java)
            } else {
                Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance().apply { set(year, month, day) }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    private fun populateSpinners(yearSpinner: Spinner, monthSpinner: Spinner, daySpinner: Spinner) {
        // Populate year spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 100..currentYear).toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearSpinner.adapter = yearAdapter

        // Populate month spinner
        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthSpinner.adapter = monthAdapter

        // Populate day spinner
        val days = (1..31).toList()
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        daySpinner.adapter = dayAdapter
    }
}
