package com.example.gym_workout

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gym_workout.database.DataManager
import com.example.gym_workout.utils.MealDetailsDialog
import com.example.gym_workout.utils.MealPlanGenerator
import java.util.Calendar
import android.util.Log

class Food : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var sharedPreferences: SharedPreferences
    private var currentDay = 0
    private val handler = Handler(Looper.getMainLooper())
    private var userDietPlan: String = "default" // Default diet plan

    private val PREFS_NAME = "MealPlanPrefs"
    private val LAST_UPDATED_KEY = "last_updated_day"
    private val LAST_UPDATE_TIME_KEY = "last_update_time"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_food, container, false)

        initializeComponents(view)
        setupClickListeners(view)

        return view
    }

    private fun initializeComponents(view: View) {
        dataManager = DataManager(requireContext())
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load user data and get diet plan
        loadUserDataAndDietPlan(view)
        loadCurrentDay()
        checkAndUpdateMeals()
        displayMealPlan(view)
    }

    private fun loadUserDataAndDietPlan(view: View) {
        val userData = dataManager.getUserData()
        userData?.let { user ->
            // Get user's diet plan from database
            userDietPlan = user.dietPlan.lowercase()
            Log.d("Food", "Loaded diet plan: $userDietPlan")

            // Calculate and display metrics
            val metrics = dataManager.calculateMetrics(user)
            displayMetrics(view, metrics)

            // Update meal plan based on diet plan
            displayMealPlan(view)
        } ?: run {
            Log.d("Food", "No user data found")
            userDietPlan = "default"
        }
    }

    private fun displayMealPlan(view: View) {
        try {
            // Get meal plan based on user's diet plan
            val mealPlan = when (userDietPlan) {
                "vegetarian" -> MealPlanGenerator.getVegetarianMealPlan()
                "vegan" -> MealPlanGenerator.getVeganMealPlan()
                "non vegetarian" -> MealPlanGenerator.getNonVegetarianMealPlan()
                else -> MealPlanGenerator.getDefaultMealPlan()
            }

            // Display meals for current day
            view.findViewById<TextView>(R.id.breakfastFood).apply {
                text = mealPlan["breakfast"]?.getOrNull(currentDay) ?: "Default breakfast"
            }
            view.findViewById<TextView>(R.id.lunchFood).apply {
                text = mealPlan["lunch"]?.getOrNull(currentDay) ?: "Default lunch"
            }
            view.findViewById<TextView>(R.id.dinnerFood).apply {
                text = mealPlan["dinner"]?.getOrNull(currentDay) ?: "Default dinner"
            }

            // Update meal type label based on diet plan
            val dietPlanLabel = when (userDietPlan) {
                "vegetarian" -> "Vegetarian"
                "vegan" -> "Vegan"
                "non vegetarian" -> "Non-Vegetarian"
                else -> "Standard"
            }
            view.findViewById<TextView>(R.id.FoodSuggestion).text =
                "$dietPlanLabel Meal Plan - Day ${currentDay + 1}"

        } catch (e: Exception) {
            Log.e("Food", "Error displaying meal plan", e)
            // Display default meals if there's an error
            displayDefaultMeals(view)
        }
    }

    private fun displayDefaultMeals(view: View) {
        view.findViewById<TextView>(R.id.breakfastFood).text = "Default healthy breakfast"
        view.findViewById<TextView>(R.id.lunchFood).text = "Default healthy lunch"
        view.findViewById<TextView>(R.id.dinnerFood).text = "Default healthy dinner"
    }

    private fun setupClickListeners(view: View) {
        // Breakfast click listener
        view.findViewById<LinearLayout>(R.id.breakfast).setOnClickListener {
            val mealName = view.findViewById<TextView>(R.id.breakfastFood).text.toString()
            showMealDetails("Breakfast", mealName)
        }

        // Lunch click listener
        view.findViewById<LinearLayout>(R.id.lunch).setOnClickListener {
            val mealName = view.findViewById<TextView>(R.id.lunchFood).text.toString()
            showMealDetails("Lunch", mealName)
        }

        // Dinner click listener
        view.findViewById<LinearLayout>(R.id.dinner).setOnClickListener {
            val mealName = view.findViewById<TextView>(R.id.dinnerFood).text.toString()
            showMealDetails("Dinner", mealName)
        }
    }

    private fun showMealDetails(mealType: String, mealName: String) {
        val nutrients = MealPlanGenerator.getNutrientsForMeal(mealName)

        // Include diet plan type in dialog
        val dialogTitle = "$userDietPlan $mealType"
        val dialog = MealDetailsDialog(requireContext(), dialogTitle, mealName, nutrients)
        dialog.show()
    }

    private fun loadCurrentDay() {
        currentDay = sharedPreferences.getInt(LAST_UPDATED_KEY, 0)
        val lastUpdateTime = sharedPreferences.getLong(LAST_UPDATE_TIME_KEY, 0)

        if (shouldUpdateDay(lastUpdateTime)) {
            currentDay = (currentDay + 1) % 7
            saveCurrentDay()
        }
    }

    private fun shouldUpdateDay(lastUpdateTime: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000L
        return currentTime - lastUpdateTime >= oneDayInMillis
    }

    private fun saveCurrentDay() {
        sharedPreferences.edit().apply {
            putInt(LAST_UPDATED_KEY, currentDay)
            putLong(LAST_UPDATE_TIME_KEY, System.currentTimeMillis())
            apply()
        }
    }

    private fun checkAndUpdateMeals() {
        val calendar = Calendar.getInstance()
        val currentTimeMillis = calendar.timeInMillis
        val lastUpdateTime = sharedPreferences.getLong(LAST_UPDATE_TIME_KEY, 0)

        if (currentTimeMillis - lastUpdateTime >= 24 * 60 * 60 * 1000) {
            currentDay = (currentDay + 1) % 7
            saveCurrentDay()
            scheduleNextUpdate()
        } else {
            val remainingTime = (24 * 60 * 60 * 1000) - (currentTimeMillis - lastUpdateTime)
            scheduleNextUpdate(remainingTime)
        }
    }

    private fun scheduleNextUpdate(delay: Long = 24 * 60 * 60 * 1000) {
        handler.postDelayed({
            currentDay = (currentDay + 1) % 7
            saveCurrentDay()
            view?.let { displayMealPlan(it) }
            scheduleNextUpdate()
        }, delay)
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
    override fun onResume() {
        super.onResume()
        // Reload user data in case diet plan changed
        view?.let {
            loadUserDataAndDietPlan(it)
            checkAndUpdateMeals()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}