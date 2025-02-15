package com.example.gym_workout.utils


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.example.gym_workout.R

class MealDetailsDialog(
    context: Context,
    private val mealType: String,
    private val mealName: String,
    private val nutrients: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meal_details_overlay)

        findViewById<TextView>(R.id.mealType).text = mealType
        findViewById<TextView>(R.id.mealName).text = mealName
        findViewById<TextView>(R.id.nutrientInfo).text = nutrients
    }
}