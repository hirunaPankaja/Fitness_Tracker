package com.example.gym_workout

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.database.DataManager
import com.example.gym_workout.database.UserData

class UserDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val dataManager = DataManager(this)
        val userData: UserData? = dataManager.getUserData()

        if (userData != null) {
            findViewById<TextView>(R.id.firstNameTextView).text = userData.firstName
            findViewById<TextView>(R.id.lastNameTextView).text = userData.lastName
            findViewById<TextView>(R.id.dateOfBirthTextView).text = userData.dateOfBirth
            findViewById<TextView>(R.id.emailTextView).text = userData.email
            findViewById<TextView>(R.id.genderTextView).text = userData.gender
            findViewById<TextView>(R.id.heightTextView).text = userData.height.toString()
            findViewById<TextView>(R.id.reasonForWeightGainTextView).text = userData.reasonForWeightGain
            findViewById<TextView>(R.id.dietPlanTextView).text = userData.dietPlan
            findViewById<TextView>(R.id.weightTextView).text = userData.weight.toString()
            findViewById<TextView>(R.id.goalTextView).text = userData.goal
            findViewById<TextView>(R.id.dailyWorkingTimeTextView).text = userData.dailyWorkingTime
            findViewById<TextView>(R.id.fitnessLevelTextView).text = userData.fitnessLevel
        }
    }
}
