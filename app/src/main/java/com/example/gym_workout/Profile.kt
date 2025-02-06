package com.example.gym_workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gym_workout.database.DataManager
import com.example.gym_workout.database.UserData

class Profile : Fragment() {

    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        val userData = dataManager.getUserData()

        userData?.let { populateUserData(it, view) }
    }

    private fun populateUserData(user: UserData, view: View) {
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val weightTextView = view.findViewById<TextView>(R.id.weightTextView)
        val heightTextView = view.findViewById<TextView>(R.id.heightTextView)
        val ageTextView = view.findViewById<TextView>(R.id.ageTextView)

        nameTextView.text = "${user.firstName} ${user.lastName}"
        weightTextView.text = "${user.weight} KG"
        heightTextView.text = "${user.height / 100.0} Ft"  // Convert height to feet
        ageTextView.text = "${dataManager.calculateAge(user.dateOfBirth)} Years"

        val metrics = dataManager.calculateMetrics(user)
        // Populate additional metrics as needed
    }
}
