package com.example.gym_workout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class JogWalking : AppCompatActivity() {
    private lateinit var walkingAnimation: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jog_walking)

        walkingAnimation = findViewById(R.id.walkingAnimation)

        // Load the animated GIF using Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.walking_man_icon) // replace with your actual GIF resource
            .into(walkingAnimation)

        walkingAnimation.setOnClickListener {
            startCountdown {
                navigateToWalkingPage()
            }
        }
    }

    private fun startCountdown(onFinish: () -> Unit) {
        val countdownHandler = Handler(Looper.getMainLooper())
        var countdown = 3
        countdownHandler.post(object : Runnable {
            override fun run() {
                if (countdown > 0) {
                    countdown--
                    countdownHandler.postDelayed(this, 1000)
                } else {
                    onFinish()
                }
            }
        })
    }

    private fun navigateToWalkingPage() {
        val intent = Intent(this, JogMapWalkingActivity::class.java)
        startActivity(intent)
    }
}
