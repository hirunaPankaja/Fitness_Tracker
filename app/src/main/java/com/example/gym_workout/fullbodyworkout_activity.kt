package com.example.gym_workout

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.database.DatabaseHelperWorkout
import com.example.gym_workout.databinding.ActivityFullbodyworkoutBinding

class fullbodyworkout_activity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelperWorkout
    private lateinit var binding: ActivityFullbodyworkoutBinding
    private val completedWorkouts = mutableListOf<String>() // Track completed workouts
    private var totalCaloriesBurned = 0
    private var totalDuration = 0 // Track total duration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullbodyworkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelperWorkout(this)
        // Insert data for all exercises
        insertExerciseData()

        binding.imgJumpingJacks.setOnClickListener { onCardClick(it) }
        binding.imgBurpees.setOnClickListener { onCardClick(it) }
        binding.imgPlankJacks.setOnClickListener { onCardClick(it) }
        binding.imgHighKnees.setOnClickListener { onCardClick(it) }
        binding.imgPushUps.setOnClickListener { onCardClick(it) }
        binding.imgBodyweightSquats.setOnClickListener { onCardClick(it) }
        binding.imgPlanktoShoulderTap.setOnClickListener { onCardClick(it) }
        binding.imgSidePlank.setOnClickListener { onCardClick(it) }

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
        // Exercise 1: Jumping Jacks
        val imgJumpingJacks = BitmapFactory.decodeResource(resources, R.drawable.jumpingjack)
        dbHelper.insertExercise(
            "JumpingJacks",
            "Jumping Jacks",
            "1. Stand with your feet together and arms by your sides.\n2. Jump and spread your legs apart while raising your arms overhead.\n3. Jump again to return to the starting position.\n4. Repeat for the set duration.",
            "3 sets of 45 sec",
            imgJumpingJacks
        )

        // Exercise 2: Burpees
        val imgBurpees = BitmapFactory.decodeResource(resources, R.drawable.burpees)
        dbHelper.insertExercise(
            "Burpees",
            "Burpees",
            "1. Start in a standing position.\n2. Drop into a squat and place your hands on the floor.\n3. Jump your feet back into a plank position.\n4. Perform a push-up, then jump your feet back to the squat position.\n5. Explode upwards into a jump and repeat.",
            "3 sets of 12 reps",
            imgBurpees
        )

        // Exercise 3: Plank Jacks
        val imgPlankJacks = BitmapFactory.decodeResource(resources, R.drawable.plankjacks)
        dbHelper.insertExercise(
            "PlankJacks",
            "Plank Jacks",
            "1. Start in a high plank position with hands under shoulders and body in a straight line.\n2. Jump your feet out to the sides, slightly wider than hip-width apart.\n3. Jump your feet back together to the starting position.\n4. Keep your upper body stable throughout the movement.",
            "3 sets of 15 sets",
            imgPlankJacks
        )

        // Exercise 4: High Knees
        val imgHighKnees = BitmapFactory.decodeResource(resources, R.drawable.highknees)
        dbHelper.insertExercise(
            "HighKnees",
            "High Knees",
            "1. Stand with your feet hip-width apart.\n2. Quickly drive your knees up toward your chest, one at a time.\n3. Pump your arms in sync with your knees.\n4. Move as fast as possible while maintaining good form.",
            "3 sets of 45 sec",
            imgHighKnees
        )

        // Exercise 5: Push-Ups
        val imgPushUps = BitmapFactory.decodeResource(resources, R.drawable.pushup)
        dbHelper.insertExercise(
            "PushUps",
            "Push-Ups",
            "1. Start in a plank position with your hands shoulder-width apart.\n2. Lower your body until your chest almost touches the floor.\n3. Push back up to the starting position, keeping your body straight.\n4. Repeat for the desired number of reps.",
            "3 sets of 12 reps",
            imgPushUps
        )

        // Exercise 6: Bodyweight Squats
        val imgBodyweightSquats = BitmapFactory.decodeResource(resources, R.drawable.squats)
        dbHelper.insertExercise(
            "BodyweightSquats",
            "Bodyweight Squats",
            "1. Stand with your feet shoulder-width apart.\n2. Lower your hips down and back as if sitting into a chair.\n3. Keep your chest upright and your knees behind your toes.\n4. Push through your heels to return to the starting position.\n5. Repeat for the desired number of reps.",
            "3 sets of 15 reps",
            imgBodyweightSquats
        )

        // Exercise 7: Plank to Shoulder Tap
        val imgPlanktoShoulderTap = BitmapFactory.decodeResource(resources, R.drawable.tricep_dips)
        dbHelper.insertExercise(
            "PlanktoShoulderTap",
            "Plank to Shoulder Tap",
            "1. Start in a high plank position with your hands under your shoulders.\n2. Tap your left shoulder with your right hand while keeping your body stable.\n3. Return your hand to the mat and repeat with the other hand.\n4. Alternate taps while keeping your core tight.",
            "3 sets of 45 sec",
            imgPlanktoShoulderTap
        )

        // Exercise 8: Side Plank
        val imgSidePlank = BitmapFactory.decodeResource(resources, R.drawable.sideplank)
        dbHelper.insertExercise(
            "SidePlank",
            "Side Plank (Each Side)",
            "1. Lie on your side with your legs straight.\n2. Prop yourself up on your elbow, keeping it directly under your shoulder.\n3. Lift your hips off the ground, forming a straight line from head to heels.\n4. Hold for the set duration, then switch sides.",
            "3 sets of 30 sec",
            imgSidePlank
        )
    }

    private fun checkCompletedWorkouts() {
        // Iterate over completed workouts and update UI
        if (completedWorkouts.contains("JumpingJacks")) {
            binding.root.findViewById<ImageView>(R.id.imgJumpingJacks).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("Burpees")) {
            binding.root.findViewById<ImageView>(R.id.imgBurpees).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("PlankJacks")) {
            binding.root.findViewById<ImageView>(R.id.imgPlankJacks).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("HighKnees")) {
            binding.root.findViewById<ImageView>(R.id.imgHighKnees).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("PushUps")) {
            binding.root.findViewById<ImageView>(R.id.imgPushUps).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("BodyweightSquats")) {
            binding.root.findViewById<ImageView>(R.id.imgBodyweightSquats).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("PlanktoShoulderTap")) {
            binding.root.findViewById<ImageView>(R.id.imgPlankShoulderTaps).visibility = View.VISIBLE
        }
        if (completedWorkouts.contains("SidePlank")) {
            binding.root.findViewById<ImageView>(R.id.imgSidePlank).visibility = View.VISIBLE
        }

        // Check if all workouts are completed
        if (completedWorkouts.containsAll(listOf("JumpingJacks", "Burpees", "PlankJacks", "HighKnees", "PushUps", "BodyweightSquats", "PlanktoShoulderTap", "SidePlank"))) {
            Log.d("activity_fullbodyworkout", "All workouts completed. Showing popup.")
            showCompletionPopup()
        } else {
            Log.d("activity_fullbodyworkout", "Workouts not completed yet.")
        }
    }

    private fun showCompletionPopup() {
        val popupDialog = Popupworkoutcomplete(this, totalCaloriesBurned, totalDuration)
        popupDialog.setOnDismissListener {
            // Return to the main screen when the popup is dismissed
            val intent = Intent(this, fullbodyworkout_activity::class.java)
            startActivity(intent)
        }
        popupDialog.show()
    }

    fun onCardClick(view: View) {
        Log.d("activity_fullbodyworkout", "Card clicked: ${view.id}")

        val exerciseName = when (view.id) {
            R.id.imgJumpingJacks -> "JumpingJacks"
            R.id.imgBurpees -> "Burpees"
            R.id.imgPlankJacks -> "PlankJacks"
            R.id.imgHighKnees -> "HighKnees"
            R.id.imgPushUps -> "PushUps"
            R.id.imgBodyweightSquats -> "BodyweightSquats"
            R.id.imgPlanktoShoulderTap -> "PlanktoShoulderTap"
            R.id.imgSidePlank -> "SidePlank"
            else -> {
                Log.e("activity_fullbodyworkout", "Unknown view ID: ${view.id}")
                return
            }
        }

        Log.d("activity_fullbodyworkout", "Exercise: $exerciseName")
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
            intent.putExtra("totalCaloriesBurned", totalCaloriesBurned)
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            Toast.makeText(this, "No such exercise found", Toast.LENGTH_SHORT).show()
        }
    }

    fun onStartWorkoutClick(view: View) {
        Log.d("activity_fullbodyworkout", "Start Workout button clicked")
        Toast.makeText(this, "Workout Started!", Toast.LENGTH_SHORT).show()

        // Define the list of exercises for the session
        val exercises = listOf("JumpingJacks", "Burpees", "PlankJacks", "HighKnees", "PushUps", "BodyweightSquats", "PlanktoShoulderTap", "SidePlank")

        // Start the first exercise
        startExercise(exercises, 0)
    }

    private fun startExercise(exercises: List<String>, currentIndex: Int) {
        if (currentIndex < exercises.size) {
            val exercise = dbHelper.getExercise(exercises[currentIndex])
            if (exercise != null) {
                totalCaloriesBurned += getCaloriesBurned(exercise.name)
                totalDuration += getExerciseDuration(exercise.name)

                val intent = Intent(this, Workout::class.java)
                intent.putExtra("title", exercise.title)
                intent.putExtra("instructions", exercise.instructions)
                intent.putExtra("repsSets", exercise.repsSets)
                intent.putExtra("exerciseName", exercise.name)
                intent.putExtra("exercises", exercises.toTypedArray())
                intent.putExtra("currentIndex", currentIndex)
                intent.putExtra("completedWorkouts", completedWorkouts.toTypedArray())
                intent.putExtra("totalCaloriesBurned", totalCaloriesBurned)
                intent.putExtra("totalDuration", totalDuration)
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "No such exercise found", Toast.LENGTH_SHORT).show()
            }
        } else {
            // All workouts completed, show completion popup
            Log.d("activity_fullbodyworkout", "All exercises completed. Showing popup.")
            showCompletionPopup()
        }
    }

    private fun getCaloriesBurned(exerciseName: String): Int {
        return when (exerciseName) {
            "JumpingJacks" -> 30
            "Burpees" -> 50
            "PlankJacks" -> 25
            "HighKnees" -> 40
            "PushUps" -> 60
            "BodyweightSquats" -> 50
            "PlanktoShoulderTap" -> 20
            "SidePlank" -> 15
            else -> 0
        }
    }

    private fun getExerciseDuration(exerciseName: String): Int {
        return when (exerciseName) {
            "JumpingJacks" -> 3 // 3 sets of 45 sec
            "Burpees" -> 3 // 3 sets of 1 min each
            "PlankJacks" -> 3 // 3 sets of 1 min each
            "HighKnees" -> 3 // 3 sets of 45 sec
            "PushUps" -> 3 // 3 sets of 1 min each
            "BodyweightSquats" -> 3 // 3 sets of 1 min each
            "PlanktoShoulderTap" -> 3 // 3 sets of 1 min each
            "SidePlank" -> 1 // 1 set of 1 min each
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
