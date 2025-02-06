package com.example.gym_workout

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.registrationSteps.Step10
import com.example.gym_workout.utils.NavigationHelper

class step1 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.step1)

        val getStartedButton = findViewById<Button>(R.id.btnGetStarted)
        getStartedButton.setOnClickListener {
            // Navigate to step2 activity
            NavigationHelper.navigateToNextStep(this, Step10::class.java)

        }
    }
}