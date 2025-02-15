package com.example.gym_workout

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.database.DatabaseHelperWorkout
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator

class Workout : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    private var timeLeftInSeconds: Long = 60 // Start with 60 seconds (1 minute)
    private val maxTimeInSeconds: Long = 60 // 60 seconds (1 minute) is the max time
    private lateinit var startButton: MaterialButton
    private lateinit var pauseButton: MaterialButton
    private lateinit var resetButton: MaterialButton
    private lateinit var timerDisplay: TextView
    private lateinit var circularProgressIndicator: CircularProgressIndicator

    // UI elements for exercise details
    private lateinit var imageViewIllustration: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewInstructions: TextView
    private lateinit var textViewRepsSets: TextView

    private lateinit var dbHelper: DatabaseHelperWorkout

    private lateinit var exercises: List<String>
    private var currentIndex: Int = 0
    private var completedWorkouts: MutableList<String> = mutableListOf()

    private var totalCaloriesBurned = 0 // Track total calories burned
    private var totalDuration = 0 // Track total duration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workout)

        // Initialize UI elements
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)
        timerDisplay = findViewById(R.id.timerDisplay)
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator)
        imageViewIllustration = findViewById(R.id.imageViewIllustration)
        textViewTitle = findViewById(R.id.textViewTitle)
        textViewInstructions = findViewById(R.id.textViewInstructions)
        textViewRepsSets = findViewById(R.id.textViewRepsSets)

        dbHelper = DatabaseHelperWorkout(this)

        // Button click listeners
        startButton.setOnClickListener { startTimer() }
        pauseButton.setOnClickListener { pauseTimer() }
        resetButton.setOnClickListener { resetTimer() }

        // Set Circular Progress Indicator
        circularProgressIndicator.max = 100
        circularProgressIndicator.progress = 100

        // Update timer text and progress
        updateTimerText()
        updateProgressIndicator()

        // Fetch data from SQLite
        fetchDataFromSQLite()

        // Retrieve the list of exercises and the current index
        exercises = intent.getStringArrayExtra("exercises")?.toList() ?: listOf()
        currentIndex = intent.getIntExtra("currentIndex", 0)
        completedWorkouts = intent.getStringArrayExtra("completedWorkouts")?.toMutableList() ?: mutableListOf()
        totalCaloriesBurned = intent.getIntExtra("totalCaloriesBurned", 0)
        totalDuration = intent.getIntExtra("totalDuration", 0)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInSeconds = millisUntilFinished / 1000
                updateTimerText()
                updateProgressIndicator()
            }

            override fun onFinish() {
                timerDisplay.text = "00:00"
                circularProgressIndicator.progress = 0
                Log.d("Workout", "Timer finished")
                goToNextExercise()
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        // Update the UI to reflect the paused state
        updateTimerText()
        updateProgressIndicator()
        Log.d("Workout", "Timer paused")
    }

    private fun resetTimer() {
        timer?.cancel()
        timeLeftInSeconds = maxTimeInSeconds
        updateTimerText()
        updateProgressIndicator()
        Log.d("Workout", "Timer reset")
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInSeconds / 60)
        val seconds = (timeLeftInSeconds % 60)
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerDisplay.text = timeFormatted
    }

    private fun updateProgressIndicator() {
        val progress = (timeLeftInSeconds.toDouble() / maxTimeInSeconds * 100).toInt()
        circularProgressIndicator.progress = progress
    }

    private fun fetchDataFromSQLite() {
        val exerciseName = intent.getStringExtra("exerciseName")
        val exercise = dbHelper.getExercise(exerciseName ?: "DefaultExercise")

        if (exercise != null) {
            textViewTitle.text = exercise.title
            textViewInstructions.text = exercise.instructions
            textViewRepsSets.text = exercise.repsSets
            imageViewIllustration.setImageBitmap(exercise.image)
            totalCaloriesBurned += getCaloriesBurned(exerciseName) // Add calories for the current exercise
            totalDuration += getExerciseDuration(exerciseName) // Add duration for the current exercise
        } else {
            Log.e("Workout", "No exercise found with name $exerciseName")
        }
    }

    private fun getCaloriesBurned(exerciseName: String?): Int {
        return when (exerciseName) {
            "PikePushUps" -> 50
            "PlankShoulderTaps" -> 40
            "PushUps" -> 60
            "SupermanHold" -> 30
            "TricepDips" -> 35
            "BodyweightSquats" -> 60
            "GluteBridges" -> 40
            "Lunges" -> 50
            "StepUps" -> 45
            "CalfRaises" -> 30
            "ChildPose" -> 10
            "DownwardDog" -> 15
            "CatCowPose" -> 10
            "WarriorIIPose" -> 20
            "CobraPose" -> 15
            "SeatedForwardBend" -> 10
            "BridgePose" -> 15
            "CorpsePose" -> 5
            "JumpingJacks" -> 30
            "Burpees" -> 50
            "PlankJacks" -> 25
            "HighKnees" -> 40
            else -> 0
        }
    }

    private fun getExerciseDuration(exerciseName: String?): Int {
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

    private fun goToNextExercise() {
        currentIndex++
        if (currentIndex < exercises.size) {
            val nextExerciseName = exercises[currentIndex]
            val nextExercise = dbHelper.getExercise(nextExerciseName)
            if (nextExercise != null) {
                val intent = Intent(this, Workout::class.java)
                intent.putExtra("title", nextExercise.title)
                intent.putExtra("instructions", nextExercise.instructions)
                intent.putExtra("repsSets", nextExercise.repsSets)
                intent.putExtra("exerciseName", nextExercise.name)
                intent.putExtra("exercises", exercises.toTypedArray())
                intent.putExtra("currentIndex", currentIndex)
                intent.putExtra("completedWorkouts", completedWorkouts.toTypedArray())
                intent.putExtra("totalCaloriesBurned", totalCaloriesBurned)
                intent.putExtra("totalDuration", totalDuration)
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                Log.e("Workout", "No such exercise found: $nextExerciseName")
            }
        } else {
            // All exercises completed
            showCompletionPopup()
        }
    }

    private fun showCompletionPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Workout Complete")
            .setMessage("Congratulations! You've completed your workout.\nTotal Calories Burned: $totalCaloriesBurned\nTotal Duration: $totalDuration minutes")
            .setPositiveButton("Done") { dialog, _ ->
                dialog.dismiss()
                val completedArray = (completedWorkouts + exercises.last()).toTypedArray()
                val intent = when {
                    exercises.containsAll(listOf("BodyweightSquats", "GluteBridges","Lunges", "StepUps", "CalfRaises")) -> {
                        Intent(this, LowerBodyWorkoutActivity::class.java)
                    }
                    exercises.containsAll(listOf("ChildPose", "DownwardDog", "CatCowPose", "WarriorIIPose", "CobraPose", "SeatedForwardBend", "BridgePose", "CorpsePose")) -> {
                        Intent(this, YogaActivity::class.java)
                    }
                    else -> {
                        Intent(this, UpperBodyWorkoutActivity::class.java)
                    }
                }
                intent.putExtra("completedWorkouts", completedArray)
                intent.putExtra("currentIndex", currentIndex)
                intent.putExtra("exercises", exercises.toTypedArray())
                intent.putExtra("totalCaloriesBurned", totalCaloriesBurned)
                intent.putExtra("totalDuration", totalDuration)
                startActivity(intent)
                finishAffinity() // Ensure all parent activities are finished
            }
            .show()
    }

    private fun checkCompletedWorkouts() {
        if (completedWorkouts.containsAll(exercises)) {
            Log.d("Workout", "All workouts completed. Showing popup.")
            showCompletionPopup()
        } else {
            Log.d("Workout", "Workouts not completed yet.")
        }
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
            Log.d("Workout", "All exercises completed. Showing popup.")
            showCompletionPopup()
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
