package com.example.gym_workout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class yoga_activity : AppCompatActivity() {

    private lateinit var move: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.yoga_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.yogaPlan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        move = findViewById(R.id.btnStartSession)
        move.setOnClickListener {
            val intent = Intent(this, Workout::class.java)
            startActivity(intent)
        }
    }
}
