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
import com.example.gym_workout.utils.SessionAdapter
import com.example.gym_workout.utils.SessionItem

class Home : Fragment() {

    private lateinit var sessionRecyclerView: RecyclerView
    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var jogLayout: LinearLayout
    private lateinit var yogaLayout: LinearLayout
    private lateinit var cyclingLayout: LinearLayout
    private lateinit var workoutLayout: LinearLayout
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

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
    }

    private fun updateSelectedBackground(selectedLayout: LinearLayout) {
        jogLayout.setBackgroundResource(R.drawable.background_square)
        yogaLayout.setBackgroundResource(R.drawable.background_square)
        cyclingLayout.setBackgroundResource(R.drawable.background_square)
        workoutLayout.setBackgroundResource(R.drawable.background_square)

        selectedLayout.setBackgroundResource(R.drawable.highlight_square)
    }
}
