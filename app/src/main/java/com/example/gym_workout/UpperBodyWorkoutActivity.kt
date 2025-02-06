package com.example.gym_workout

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.Workout

class UpperBodyWorkoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upperbodyworkout_activity)
    }

    fun onCardClick(view: View) {
        Log.d("UpperBodyWorkoutActivity", "Card clicked: ${view.id}")

        val exerciseName = when (view.id) {
            R.id.imgPushUps -> "PushUps"
            R.id.imgDips -> "Dips"
            R.id.imgSquats -> "Squats"
            R.id.imgLunges -> "Lunges"
            R.id.imgSupermanHold -> "SupermanHold"
            R.id.imgGluteBridges -> "GluteBridges"
            else -> {
                Log.e("UpperBodyWorkoutActivity", "Unknown view ID: ${view.id}")
                return
            }
        }

        Log.d("UpperBodyWorkoutActivity", "Exercise: $exerciseName")
        val intent = Intent(this, Workout::class.java)
        intent.putExtra("exerciseName", exerciseName)
        startActivity(intent)
    }

    fun onStartWorkoutClick(view: View) {
        Log.d("UpperBodyWorkoutActivity", "Start Workout button clicked")

        val fadeIn = AnimatorInflater.loadAnimator(this, R.animator.fade_in) as AnimatorSet
        fadeIn.setTarget(view)
        fadeIn.start()

        Toast.makeText(this, "Workout Started!", Toast.LENGTH_SHORT).show()

        // Start Workout activity with a default exercise or specific name
        val intent = Intent(this, Workout::class.java)
        intent.putExtra("exerciseName", "DefaultExercise") // Replace with specific exercise if needed
        startActivity(intent)
    }
}
