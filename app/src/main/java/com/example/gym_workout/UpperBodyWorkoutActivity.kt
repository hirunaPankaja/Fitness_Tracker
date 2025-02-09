package com.example.gym_workout

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.database.DatabaseHelperWorkout
import com.example.gym_workout.databinding.UpperbodyworkoutActivityBinding

class UpperBodyWorkoutActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelperWorkout
    private lateinit var binding: UpperbodyworkoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpperbodyworkoutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelperWorkout(this)

        // Insert data for all exercises
        insertExerciseData()

        binding.imgPikePushUps.setOnClickListener { onCardClick(it) }
        binding.imgPlankShoulderTaps.setOnClickListener { onCardClick(it) }
        binding.imgPushUps.setOnClickListener { onCardClick(it) }
        binding.imgSupermanHold.setOnClickListener { onCardClick(it) }
        binding.imgTricepDips.setOnClickListener { onCardClick(it) }

        binding.btnStartSession.setOnClickListener { onStartWorkoutClick(it) }
    }

    private fun insertExerciseData() {
        // Exercise 1: Pike Push-Ups
        val pikePushUpsImage = BitmapFactory.decodeResource(resources, R.drawable.pike_pushups)
        dbHelper.insertExercise(
            "PikePushUps",
            "Pike Push-Ups",
            "1. Start in a downward dog position with your hips lifted and hands on the floor.\n2. Bend your elbows and lower the top of your head toward the ground.\n3. Push back up to the starting position.\n4. Keep your core engaged and your movements controlled.",
            "3 sets of 10 reps",
            pikePushUpsImage
        )

        // Exercise 2: Plank Shoulder Taps
        val plankShoulderTapsImage = BitmapFactory.decodeResource(resources, R.drawable.reverse_plank)
        dbHelper.insertExercise(
            "PlankShoulderTaps",
            "Plank Shoulder Taps",
            "1. Start in a high plank position, hands under your shoulders, body in a straight line.\n2. Without shifting your hips, tap your left shoulder with your right hand.\n3. Return your hand to the ground and repeat with the opposite side.\n4. Continue alternating sides while maintaining a stable core.",
            "3 sets of 10 reps",
            plankShoulderTapsImage
        )

        // Exercise 3: Push-Ups
        val pushUpsImage = BitmapFactory.decodeResource(resources, R.drawable.pushup)
        dbHelper.insertExercise(
            "PushUps",
            "Push-Ups",
            "1. Start in a high plank position with your hands slightly wider than shoulder-width apart.\n2. Keep your body in a straight line from head to heels; engage your core.\n3. Lower your chest towards the floor, bending your elbows at a 90-degree angle.\n4. Push through your palms to return to the starting position.",
            "3 sets of 10 reps",
            pushUpsImage
        )

        // Exercise 4: Superman Hold
        val supermanHoldImage = BitmapFactory.decodeResource(resources, R.drawable.superman_holds)
        dbHelper.insertExercise(
            "SupermanHold",
            "Superman Hold",
            "1. Lie face down with arms extended forward and legs straight.\n2. Lift your arms, chest, and legs off the ground as high as you can.\n3. Hold for the specified time, squeezing your glutes and lower back.\n4. Slowly lower yourself back to the floor.",
            "3 sets of 30 sec",
            supermanHoldImage
        )

        // Exercise 5: Tricep Dips
        val tricepDipsImage = BitmapFactory.decodeResource(resources, R.drawable.tricep_dips)
        dbHelper.insertExercise(
            "TricepDips",
            "Tricep Dips",
            "1. Sit on the edge of a sturdy chair or sofa, hands next to your hips, fingers pointing forward.\n2. Slide your hips off the edge and support yourself with straight arms.\n3. Lower your body by bending your elbows, keeping them close to your sides.\n4. Push back up to the starting position by straightening your arms.",
            "3 sets of 8 reps",
            tricepDipsImage
        )
    }

    fun onCardClick(view: View) {
        Log.d("UpperBodyWorkoutActivity", "Card clicked: ${view.id}")

        val exerciseName = when (view.id) {
            R.id.imgPikePushUps -> "PikePushUps"
            R.id.imgPlankShoulderTaps -> "PlankShoulderTaps"
            R.id.imgPushUps -> "PushUps"
            R.id.imgSupermanHold -> "SupermanHold"
            R.id.imgTricepDips -> "TricepDips"
            else -> {
                Log.e("UpperBodyWorkoutActivity", "Unknown view ID: ${view.id}")
                return
            }
        }

        Log.d("UpperBodyWorkoutActivity", "Exercise: $exerciseName")
        fetchExerciseDetails(exerciseName)
    }

    private fun fetchExerciseDetails(exerciseName: String) {
        val exercise = dbHelper.getExercise(exerciseName)
        if (exercise != null) {
            val intent = Intent(this, Workout::class.java)
            intent.putExtra("title", exercise.title)
            intent.putExtra("instructions", exercise.instructions)
            intent.putExtra("repsSets", exercise.repsSets)
            intent.putExtra("exerciseName", exercise.name) // For fetching image
            startActivity(intent)
        } else {
            Toast.makeText(this, "No such exercise found", Toast.LENGTH_SHORT).show()
        }
    }

    fun onStartWorkoutClick(view: View) {
        Log.d("UpperBodyWorkoutActivity", "Start Workout button clicked")
        Toast.makeText(this, "Workout Started!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Workout::class.java)
        intent.putExtra("exerciseName", "DefaultExercise")
        startActivity(intent)
    }
}
