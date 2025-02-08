package com.example.gym_workout

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gym_workout.database.DataManager
import android.util.Log
import com.example.gym_workout.utils.MealPlanGenerator

class Food : Fragment() {

    private lateinit var dataManager: DataManager
    private var currentDay = 0 // Tracks the current day (0-6)
    private val handler = Handler(Looper.getMainLooper()) // Handler for automatic updates
    private val updateInterval = 24 * 60 * 60 * 1000L // 24 hours in milliseconds

    // SharedPreferences key for storing the last updated day
    private val sharedPreferencesKey = "last_updated_day"
    private lateinit var sharedPreferences: SharedPreferences

    // Runnable to update the meal plan
    private val updateMealPlanRunnable = object : Runnable {
        override fun run() {
            currentDay = (currentDay + 1) % 7 // Cycle through days (0-6)
            saveLastUpdatedDay(currentDay) // Save the current day to SharedPreferences
            displayMealPlan(requireView()) // Update the UI
            handler.postDelayed(this, updateInterval) // Schedule the next update after 24 hours
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_food, container, false)

        dataManager = DataManager(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("meal_plan_prefs", Context.MODE_PRIVATE)

        loadUserData(view) // Load the user data and display metrics
        loadLastUpdatedDay() // Load the last updated day from SharedPreferences
        displayMealPlan(view) // Display meal plan for the current day

        return view
    }

    override fun onResume() {
        super.onResume()
        // Start automatic updates when the fragment is visible
        handler.postDelayed(updateMealPlanRunnable, updateInterval)
    }

    override fun onPause() {
        super.onPause()
        // Stop automatic updates when the fragment is not visible
        handler.removeCallbacks(updateMealPlanRunnable)
    }

    private fun loadUserData(view: View) {
        val userData = dataManager.getUserData()
        userData?.let { user ->
            val metrics = dataManager.calculateMetrics(user)
            displayMetrics(view, metrics)
        } ?: run {
            Log.d("Food", "No user data found")
        }
    }

    private fun displayMetrics(view: View, metrics: Map<String, Any>) {
        val bmi = metrics["BMI"] as Double
        val waterIntake = metrics["Daily Water Intake"] as Int
        val caloricNeeds = metrics["Daily Caloric Needs"] as Int
        val nutrientNeeds = metrics["Nutrient Needs"] as Map<String, Int>
        val sleepNeed = metrics["Sleep Need"] as Int

        view.findViewById<TextView>(R.id.bmiTextView).text = getString(R.string.bmi_text, bmi)
        view.findViewById<TextView>(R.id.waterIntakeTextView).text = getString(R.string.water_intake_text, waterIntake)
        view.findViewById<TextView>(R.id.caloricNeedsTextView).text = getString(R.string.caloric_needs_text, caloricNeeds)
        view.findViewById<TextView>(R.id.proteinTextView).text = getString(R.string.protein_text, nutrientNeeds["protein"])
        view.findViewById<TextView>(R.id.fatTextView).text = getString(R.string.fat_text, nutrientNeeds["fat"])
        view.findViewById<TextView>(R.id.carbsTextView).text = getString(R.string.carbs_text, nutrientNeeds["carbs"])
        view.findViewById<TextView>(R.id.sleepNeedTextView).text = getString(R.string.sleep_need_text, sleepNeed)
    }

    private fun displayMealPlan(view: View) {
        val userData = dataManager.getUserData()
        userData?.let { user ->
            val mealPlan = MealPlanGenerator.getMealPlan(user.dietPlan)

            // Get the meal for the current day
            val breakfastMeal = mealPlan["breakfast"]?.get(currentDay)
            val lunchMeal = mealPlan["lunch"]?.get(currentDay)
            val dinnerMeal = mealPlan["dinner"]?.get(currentDay)

            // Display the meal names
            view.findViewById<TextView>(R.id.breakfastFood).text = breakfastMeal
            view.findViewById<TextView>(R.id.lunchFood).text = lunchMeal
            view.findViewById<TextView>(R.id.dinnerFood).text = dinnerMeal
        }
    }

    private fun loadLastUpdatedDay() {
        // Load the last updated day from SharedPreferences
        currentDay = sharedPreferences.getInt(sharedPreferencesKey, 0)
    }

    private fun saveLastUpdatedDay(day: Int) {
        // Save the current day to SharedPreferences
        sharedPreferences.edit().putInt(sharedPreferencesKey, day).apply()
    }
}