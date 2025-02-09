package com.example.gym_workout

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.util.Log
import android.content.Intent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.database.DatabaseHelperWorkout
import com.example.gym_workout.databinding.LowerbodyworkoutActivityBinding

class LowerBodyWorkoutActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelperWorkout
    private lateinit var binding: LowerbodyworkoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LowerbodyworkoutActivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        dbHelper = DatabaseHelperWorkout(this)

        // Insert data for all exercises
        insertExerciseData()

        // Set click listeners for the exercise images
        binding.imgSquats.setOnClickListener { onCardClick(it) }
        binding.imggluteBridges.setOnClickListener { onCardClick(it) }
        binding.imgLunges.setOnClickListener { onCardClick(it) }
        binding.imgStepUps.setOnClickListener { onCardClick(it) }
        binding.imgCalfRaises.setOnClickListener { onCardClick(it) }
    }

    private fun insertExerciseData() {
        // Exercise 1: Bodyweight Squats
        val bodyweightSquatsImage = BitmapFactory.decodeResource(resources, R.drawable.squats)
        dbHelper.insertExercise(
            "BodyweightSquats",
            "Bodyweight Squats",
            "1. Stand with feet shoulder-width apart, toes slightly outward.\n2. Lower your hips by bending your knees and pushing them outward.\n3. Go as low as possible while keeping your chest upright.\n4. Push through your heels to stand back up.",
            "3 sets of 12 reps",
            bodyweightSquatsImage
        )

        // Exercise 2: Glute Bridges
        val gluteBridgesImage = BitmapFactory.decodeResource(resources, R.drawable.glutebridges)
        dbHelper.insertExercise(
            "GluteBridges",
            "Glute Bridges",
            "1. Lie on your back, knees bent, feet flat on the ground.\n2. Press through your heels and lift your hips towards the ceiling.\n3. Squeeze your glutes at the top, forming a straight line from knees to shoulders.\n4. Lower your hips back down and repeat.",
            "3 sets of 10 reps",
            gluteBridgesImage
        )

        // Exercise 3: Lunges
        val lungesImage = BitmapFactory.decodeResource(resources, R.drawable.lunges)
        dbHelper.insertExercise(
            "Lunges",
            "Lunges",
            "1. Stand with feet together, hands on hips.\n2. Step forward with your right leg, lowering your hips until both knees form 90-degree angles.\n3. Push through your front heel to return to standing.\n4. Repeat on the opposite leg.",
            "4 sets of 8 reps",
            lungesImage
        )

        // Exercise 4: Step-Ups
        val stepUpsImage = BitmapFactory.decodeResource(resources, R.drawable.step_ups)
        dbHelper.insertExercise(
            "StepUps",
            "Step-Ups",
            "1. Stand in front of a sturdy elevated surface (like a step or low chair).\n2. Place your right foot on the surface and push through your heel to lift your body.\n3. Step back down with control.\n4. Repeat on the opposite leg.",
            "3 sets of 10 reps",
            stepUpsImage
        )

        // Exercise 5: Calf Raises
        val calfRaisesImage = BitmapFactory.decodeResource(resources, R.drawable.calf_raises)
        dbHelper.insertExercise(
            "CalfRaises",
            "Calf Raises",
            "1. Stand with feet hip-width apart, hands on your hips or at your sides.\n2. Lift your heels off the ground as high as you can, balancing on your toes.\n3. Hold the position for a second, then slowly lower your heels back down.\n4. Repeat for the specified number of repetitions.",
            "4 sets of 10 reps",
            calfRaisesImage
        )
    }

    fun onCardClick(view: View) {
        Log.d("LowerBodyWorkoutActivity", "Card clicked: ${view.id}")

        val exerciseName = when (view.id) {
            R.id.imgSquats -> "BodyweightSquats"
            R.id.imggluteBridges -> "GluteBridges"
            R.id.imgLunges -> "Lunges"
            R.id.imgStepUps -> "StepUps"
            R.id.imgCalfRaises -> "CalfRaises"
            else -> {
                Log.e("LowerBodyWorkoutActivity", "Unknown view ID: ${view.id}")
                return
            }
        }

        Log.d("LowerBodyWorkoutActivity", "Exercise: $exerciseName")
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
}
