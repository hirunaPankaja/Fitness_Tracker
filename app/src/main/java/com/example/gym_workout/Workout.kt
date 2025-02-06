package com.example.gym_workout

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore

class Workout : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    private var timeLeftInSeconds: Long = 30 // Start with 30 seconds
    private val maxTimeInSeconds: Long = 30 // 30 seconds is the max time
    private lateinit var startButton: MaterialButton
    private lateinit var pauseButton: MaterialButton
    private lateinit var resetButton: MaterialButton
    private lateinit var timerDisplay: TextView
    private lateinit var circularProgressIndicator: CircularProgressIndicator

    // Firestore and UI elements
    private lateinit var db: FirebaseFirestore
    private lateinit var imageViewIllustration: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewInstructions: TextView
    private lateinit var textViewRepsSets: TextView

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

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

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

        // Fetch data from Firestore
        fetchDataFromFirestore()
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
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        // Update the UI to reflect the paused state
        updateTimerText()
        updateProgressIndicator()
    }

    private fun resetTimer() {
        timer?.cancel()
        timeLeftInSeconds = maxTimeInSeconds
        updateTimerText()
        updateProgressIndicator()
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

    private fun fetchDataFromFirestore() {
        db.collection("exercises")
            .document("PushUps")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val title = documentSnapshot.getString("name")
                    val instructions = documentSnapshot.getString("instructions")
                    val repsSets = documentSnapshot.getString("reps")

                    textViewTitle.text = title
                    textViewInstructions.text = instructions
                    textViewRepsSets.text = repsSets

                    // Log the retrieved data
                    Log.d("Firestore", "Title: $title")
                    Log.d("Firestore", "Instructions: $instructions")
                    Log.d("Firestore", "Reps and Sets: $repsSets")
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching data", e)
            }
    }
}
