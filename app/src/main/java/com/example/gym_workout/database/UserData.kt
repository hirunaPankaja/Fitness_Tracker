package com.example.gym_workout.database

object UserDataHolder {
    var userData = UserData()
}

data class UserData(
    var firstName: String = "",
    var lastName: String = "",
    var dateOfBirth: String = "",
    var gender: String = "",
    var height: Int =0 ,
    val reasonForWeightGain: String = "",
    val dietPlan: String ="",
    var weight: Int =0,
    var goal: String = "",
    var dailyWorkingTime: String = "",
    var fitnessLevel: String = "",
    var email: String = "",
    var password: String = "",


    var stepCompleted: Int = 0
)
