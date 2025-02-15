package com.example.gym_workout

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.database.DatabaseHelperWorkout
import com.example.gym_workout.databinding.LowerbodyworkoutActivityBinding

class LowerBodyWorkoutActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelperWorkout
    private lateinit var binding: LowerbodyworkoutActivityBinding
    private val completedWorkouts = mutableSetOf<String>() // Track completed workouts
    private var totalCaloriesBurned = 0
    private var totalDuration = 0 // in minutes

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

        binding.btnStartSession.setOnClickListener { onStartWorkoutClick(it) }
        binding.btnBack.setOnClickListener { onBackClick(it) }

        // Check for completed workouts
        val completedArray = intent.getStringArrayExtra("completedWorkouts") ?: arrayOf()
        completedWorkouts.addAll(completedArray.asList())

        // Check completed workouts and update UI
        checkCompletedWorkouts()
    }

    private fun onBackClick(view: View) {
        finish()
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

    private fun checkCompletedWorkouts() {
        // Iterate over completed workouts and update UI
        if (completedWorkouts.contains("BodyweightSquats")) {
            binding.root.findViewById<ImageView>(R.id.imgSquats).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("GluteBridges")) {
            binding.root.findViewById<ImageView>(R.id.imggluteBridges).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("Lunges")) {
            binding.root.findViewById<ImageView>(R.id.imgLunges).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("StepUps")) {
            binding.root.findViewById<ImageView>(R.id.imgStepUps).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("CalfRaises")) {
            binding.root.findViewById<ImageView>(R.id.imgCalfRaises).visibility = View.VISIBLE
        }

        // Check if all workouts are completed
        if (completedWorkouts.containsAll(listOf("BodyweightSquats", "GluteBridges", "Lunges", "StepUps", "CalfRaises"))) {
            Log.d("LowerBodyWorkoutActivity", "All workouts completed. Showing popup.")
            showCompletionPopup()
        } else {
            Log.d("LowerBodyWorkoutActivity", "Workouts not completed yet.")
        }
    }

    private fun showCompletionPopup() {
        val popupDialog = Popupworkoutcomplete(this, totalCaloriesBurned, totalDuration)
        popupDialog.setOnDismissListener {
            // Return to the main screen when the popup is dismissed
            val intent = Intent(this, LowerBodyWorkoutActivity::class.java)
            startActivity(intent)
        }
        popupDialog.show()
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
            intent.putExtra("exerciseName", exercise.name)
            intent.putExtra("completedWorkouts", completedWorkouts.toTypedArray())
            intent.putExtra("totalCaloriesBurned", totalCaloriesBurned) // Pass total calories burned
            intent.putExtra("totalDuration", totalDuration) // Pass total duration
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            Toast.makeText(this, "No such exercise found", Toast.LENGTH_SHORT).show()
        }
    }

    fun onStartWorkoutClick(view: View) {
        Log.d("LowerBodyWorkoutActivity", "Start Workout button clicked")
        Toast.makeText(this, "Workout Started!", Toast.LENGTH_SHORT).show()

        // Define the list of exercises for the session
        val exercises = listOf("BodyweightSquats", "GluteBridges", "Lunges", "StepUps", "CalfRaises")

        // Start the first exercise
        startExercise(exercises, 0)
    }

    private fun startExercise(exercises: List<String>, currentIndex: Int) {
        if (currentIndex < exercises.size) {
            val exercise = dbHelper.getExercise(exercises[currentIndex])
            if (exercise != null) {
                totalCaloriesBurned += getCaloriesBurned(exercise.name)
                totalDuration += getDuration(exercise.name)

                val intent = Intent(this, Workout::class.java)
                intent.putExtra("title", exercise.title)
                intent.putExtra("instructions", exercise.instructions)
                intent.putExtra("repsSets", exercise.repsSets)
                intent.putExtra("exerciseName", exercise.name)
                intent.putExtra("exercises", exercises.toTypedArray())
                intent.putExtra("currentIndex", currentIndex)
                intent.putExtra("completedWorkouts", completedWorkouts.toTypedArray())
                intent.putExtra("totalCaloriesBurned", totalCaloriesBurned) // Pass total calories burned
                intent.putExtra("totalDuration", totalDuration) // Pass total duration
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "No such exercise found", Toast.LENGTH_SHORT).show()
            }
        } else {
            // All workouts completed, show completion popup
            Log.d("LowerBodyWorkoutActivity", "All exercises completed. Showing popup.")
            showCompletionPopup()
        }
    }

    private fun getCaloriesBurned(exerciseName: String): Int {
        return when (exerciseName) {
            "BodyweightSquats" -> 60
            "GluteBridges" -> 40
            "Lunges" -> 50
            "StepUps" -> 45
            "CalfRaises" -> 30
            else -> 0
        }
    }

    private fun getDuration(exerciseName: String): Int {
        return when (exerciseName) {
            "BodyweightSquats" -> 12
            "GluteBridges" -> 10
            "Lunges" -> 8
            "StepUps" -> 10
            "CalfRaises" -> 7
            else -> 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val completedArray = data?.getStringArrayExtra("completedWorkouts") ?: arrayOf()
            completedWorkouts.addAll(completedArray.asList())

            val currentIndex = data?.getIntExtra("currentIndex", -1) ?: -1
            val exercises = data?.getStringArrayExtra("exercises")?.toList() ?: emptyList()
            totalCaloriesBurned = data?.getIntExtra("totalCaloriesBurned", 0) ?: 0
            totalDuration = data?.getIntExtra("totalDuration", 0) ?: 0

            if (currentIndex != -1) {
                startExercise(exercises, currentIndex + 1)
            } else {
                checkCompletedWorkouts()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 1
    }
}
