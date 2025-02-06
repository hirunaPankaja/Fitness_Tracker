// UserPreferences.kt
package com.example.gym_workout.database

import android.content.Context
import android.content.SharedPreferences
import com.example.gym_workout.database.UserData

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_data_pref", Context.MODE_PRIVATE)

    // Function to save the UserData object to SharedPreferences
    fun saveUserData(userData: UserData) {
        with(sharedPreferences.edit()) {
            putString("firstName", userData.firstName)
            putString("lastName", userData.lastName)
            putString("dateOfBirth", userData.dateOfBirth)
            putString("gender", userData.gender)
            putInt("height", userData.height)
            putString("reasonForWeightGain", userData.reasonForWeightGain)
            putString("dietPlan", userData.dietPlan)
            putInt("weight", userData.weight)
            putString("goal", userData.goal)
            putString("dailyWorkingTime", userData.dailyWorkingTime)
            putString("fitnessLevel", userData.fitnessLevel)
            putString("email", userData.email)
            putString("password", userData.password)
            putInt("stepCompleted", userData.stepCompleted)
            apply()  // Use apply() for asynchronous saving
        }
    }

    // Function to retrieve UserData from SharedPreferences
    fun getUserData(): UserData {
        return UserData(
            firstName = sharedPreferences.getString("firstName", "") ?: "",
            lastName = sharedPreferences.getString("lastName", "") ?: "",
            dateOfBirth = sharedPreferences.getString("dateOfBirth", "") ?: "",
            gender = sharedPreferences.getString("gender", "") ?: "",
            height = sharedPreferences.getInt("height", 0),
            reasonForWeightGain = sharedPreferences.getString("reasonForWeightGain", "") ?: "",
            dietPlan = sharedPreferences.getString("dietPlan", "") ?: "",
            weight = sharedPreferences.getInt("weight", 0),
            goal = sharedPreferences.getString("goal", "") ?: "",
            dailyWorkingTime = sharedPreferences.getString("dailyWorkingTime", "") ?: "",
            fitnessLevel = sharedPreferences.getString("fitnessLevel", "") ?: "",
            email = sharedPreferences.getString("email", "") ?: "",
            password = sharedPreferences.getString("password", "") ?: "",
            stepCompleted = sharedPreferences.getInt("stepCompleted", 0)
        )
    }
}
