package com.example.gym_workout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_workout.databinding.FragmentHomeBinding
import com.example.gym_workout.database.DatabaseHelper2
import com.example.gym_workout.utils.SessionAdapter
import com.example.gym_workout.utils.SessionItem
import java.text.SimpleDateFormat
import java.util.*

class Home : Fragment() {

    private lateinit var sessionRecyclerView: RecyclerView
    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var jogLayout: LinearLayout
    private lateinit var yogaLayout: LinearLayout
    private lateinit var cyclingLayout: LinearLayout
    private lateinit var workoutLayout: LinearLayout
    private lateinit var footstepsCountsTextView: TextView
    private lateinit var caloriesCountsTextView: TextView
    private lateinit var databaseHelper: DatabaseHelper2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper2(requireContext())

        footstepsCountsTextView = view.findViewById(R.id.Footsteps_counts)
        caloriesCountsTextView = view.findViewById(R.id.calories_value)


        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val (stepsCount, calories) = getStepsAndCalories(currentDate)


        caloriesCountsTextView.text = "$calories kcal"
        footstepsCountsTextView.text = "$stepsCount steps"

        // Initialize RecyclerView
        sessionRecyclerView = view.findViewById(R.id.session_viewer)
        sessionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        sessionAdapter = SessionAdapter(mutableListOf())
        sessionRecyclerView.adapter = sessionAdapter

        // Initialize program layouts
        jogLayout = view.findViewById(R.id.jog)
        yogaLayout = view.findViewById(R.id.yoga)
        cyclingLayout = view.findViewById(R.id.cycling)
        workoutLayout = view.findViewById(R.id.workout)

        // Set click listeners
        jogLayout.setOnClickListener {
            updateRecyclerView(getJogSessions())
            updateSelectedBackground(jogLayout)
        }

        yogaLayout.setOnClickListener {
            updateRecyclerView(getYogaSessions())
            updateSelectedBackground(yogaLayout)
        }

        cyclingLayout.setOnClickListener {
            updateRecyclerView(getCyclingSessions())
            updateSelectedBackground(cyclingLayout)
        }

        workoutLayout.setOnClickListener {
            updateRecyclerView(getWorkoutSessions())
            updateSelectedBackground(workoutLayout)

        }

        // Default selection
        updateRecyclerView(getJogSessions())
        updateSelectedBackground(jogLayout)

        return view
    }

    private fun getStepsAndCalories(date: String): Pair<Int, Int> {
        return try {
            databaseHelper.getStepsAndCaloriesForDate(date)
        } catch (e: Exception) {
            Log.e("Activity", "Error fetching steps and calories: ${e.message}")
            Pair(0, 0)
        }
    }
    private fun updateRecyclerView(sessionItems: List<SessionItem>) {
        sessionAdapter.updateData(sessionItems)
    }

    private fun updateSelectedBackground(selectedLayout: LinearLayout) {
        // Reset all backgrounds
        jogLayout.setBackgroundResource(R.drawable.background_square)
        yogaLayout.setBackgroundResource(R.drawable.background_square)
        cyclingLayout.setBackgroundResource(R.drawable.background_square)
        workoutLayout.setBackgroundResource(R.drawable.background_square)

        // Set selected background
        selectedLayout.setBackgroundResource(R.drawable.highlight_square)
    }

    private fun getJogSessions(): List<SessionItem> {
        return listOf(
            SessionItem("Walking", "30 mins", R.drawable.walking),
            SessionItem("Running", "45 mins", R.drawable.running)
        )
    }

    private fun getYogaSessions(): List<SessionItem> {
        return listOf(
            SessionItem("Yoga", "60 mins", R.drawable.yoga_icon)
        )
    }

    private fun getCyclingSessions(): List<SessionItem> {
        return listOf(
            SessionItem("Cycling", "45 mins", R.drawable.cycling)
        )
    }

    private fun getWorkoutSessions(): List<SessionItem> {
        return listOf(
            SessionItem("Upper Body", "30 mins", R.drawable.upper_body),
            SessionItem("Lower Body", "30 mins", R.drawable.lower_body)
        )
    }

}
