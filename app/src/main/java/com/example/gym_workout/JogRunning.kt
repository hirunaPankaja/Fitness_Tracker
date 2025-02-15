package com.example.gym_workout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class JogRunning : AppCompatActivity() {

    private lateinit var settingsOverlay: ConstraintLayout
    private lateinit var startButton: FloatingActionButton
    private lateinit var overlayTrigger: ImageView
    private lateinit var runningAnimation: ImageView
    private lateinit var distanceGoalInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jog_running)

        settingsOverlay = findViewById(R.id.settingsOverlay)
        overlayTrigger = findViewById(R.id.overlayTrigger)
        runningAnimation = findViewById(R.id.runningAnimation)
        distanceGoalInput = findViewById(R.id.distanceGoalInput)

        overlayTrigger.setOnClickListener {
            settingsOverlay.visibility = View.VISIBLE
        }

        runningAnimation.setOnClickListener {
            val distanceGoal = distanceGoalInput.text.toString()
            if (distanceGoal.isNotEmpty()) {
                settingsOverlay.visibility = View.GONE
                startCountdown {
                    navigateToRunningPage(distanceGoal.toInt())
                }
            } else {
                // Handle the case where the distance goal is empty or invalid
                navigateToRunningPage(5)  // Example default distance goal
            }
        }

        // Load the animated GIF using Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.running_man) // replace with your actual GIF resource
            .into(runningAnimation)
    }

    private fun startCountdown(onFinish: () -> Unit) {
        val countdownHandler = Handler(Looper.getMainLooper())
        var countdown = 3

        countdownHandler.post(object : Runnable {
            override fun run() {
                if (countdown > 0) {
                    // Update your UI with the countdown (e.g., show a countdown text)
                    // e.g., countdownTextView.text = countdown.toString()
                    countdown--
                    countdownHandler.postDelayed(this, 1000)
                } else {
                    onFinish()
                }
            }
        })
    }

    private fun navigateToRunningPage(distanceGoal: Int) {
        val intent = Intent(this, JogMap::class.java)
        intent.putExtra("DISTANCE_GOAL", distanceGoal)
        startActivity(intent)
    }
}
