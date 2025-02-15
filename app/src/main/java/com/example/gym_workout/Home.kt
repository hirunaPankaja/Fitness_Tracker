package com.example.gym_workout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.gym_workout.database.DataManager
import com.example.gym_workout.utils.HomeViewModel
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
    private lateinit var viewModel: HomeViewModel
    private lateinit var footstepsCountsTextView: TextView
    private lateinit var databaseHelper: DatabaseHelper2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper2(requireContext())

        // Initialize TextView
        footstepsCountsTextView = view.findViewById(R.id.Footsteps_counts)

        // Get current date steps count and set to TextView
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val stepsCount = getStepsCountForDate(currentDate)
        footstepsCountsTextView.text = stepsCount.toString()

        // Initialize RecyclerView

        sessionRecyclerView = view.findViewById(R.id.session_viewer)
        sessionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        sessionAdapter = SessionAdapter(mutableListOf())
        sessionRecyclerView.adapter = sessionAdapter

        jogLayout = view.findViewById(R.id.jog)
        yogaLayout = view.findViewById(R.id.yoga)
        cyclingLayout = view.findViewById(R.id.cycling)
        workoutLayout = view.findViewById(R.id.workout)

        jogLayout.setOnClickListener {
            viewModel.updateSessions("jog")
            updateSelectedBackground(jogLayout)
        }

        yogaLayout.setOnClickListener {
            viewModel.updateSessions("yoga")
            updateSelectedBackground(yogaLayout)
        }

        cyclingLayout.setOnClickListener {
            viewModel.updateSessions("cycling")
            updateSelectedBackground(cyclingLayout)
        }

        workoutLayout.setOnClickListener {
            viewModel.updateSessions("workout")
            updateSelectedBackground(workoutLayout)

        }

        // Observe the LiveData from ViewModel
        viewModel.sessions.observe(viewLifecycleOwner, Observer {
            sessionAdapter.updateData(it)
        })

        // Load user goal and set the default selection
        loadUserGoalAndSetDefaultSelection()

        return view
    }


    private fun loadUserGoalAndSetDefaultSelection() {
        val user = DataManager(requireContext()).getUserData()
        if (user != null) {
            val goal = user.goal
            Log.d("HomeFragment", "User goal: $goal")
            when (goal.trim()) {
                "Loose Weight" -> {
                    viewModel.updateSessions("Loose Weight")
                    updateSelectedBackground(workoutLayout)
                    Log.d("HomeFragment", "Displaying Fat Loss Workout Sessions")
                }
                "MuscleGain" -> {
                    viewModel.updateSessions("muscleGain")
                    updateSelectedBackground(workoutLayout)
                    Log.d("HomeFragment", "Displaying Muscle Gain Workout Sessions")
                }
                else -> {
                    viewModel.updateSessions("workout")
                    updateSelectedBackground(workoutLayout)
                    Log.d("HomeFragment", "Displaying all workout sessions")
                }
            }
        } else {
            viewModel.updateSessions("jog")
            updateSelectedBackground(jogLayout)
            Log.d("HomeFragment", "No user data found, displaying Jog Sessions")
        }

    private fun getStepsCountForDate(date: String): Int {
        return databaseHelper.getStepsCountForDate(date)
    }

    private fun updateRecyclerView(sessionItems: List<SessionItem>) {
        sessionAdapter.updateData(sessionItems)

    }

    private fun updateSelectedBackground(selectedLayout: LinearLayout) {
        jogLayout.setBackgroundResource(R.drawable.background_square)
        yogaLayout.setBackgroundResource(R.drawable.background_square)
        cyclingLayout.setBackgroundResource(R.drawable.background_square)
        workoutLayout.setBackgroundResource(R.drawable.background_square)

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
