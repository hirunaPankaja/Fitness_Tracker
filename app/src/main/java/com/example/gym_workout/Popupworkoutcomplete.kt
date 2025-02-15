package com.example.gym_workout

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Popupworkoutcomplete(context: Context, private val totalCaloriesBurned: Int, private val totalDuration: Int) : Dialog(context) {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_workout_complete)

        val tvCaloriesBurned: TextView = findViewById(R.id.tvCaloriesBurned)
        val tvTotalDuration: TextView = findViewById(R.id.tvTotalDuration)
        val btnOk: Button = findViewById(R.id.btnOk)

        tvCaloriesBurned.text = "Total Calories Burned: $totalCaloriesBurned"
        tvTotalDuration.text = "Total Duration: $totalDuration minutes"

        btnOk.setOnClickListener {
            dismiss()
        }
    }
}
