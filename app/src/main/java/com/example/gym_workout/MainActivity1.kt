package com.example.gym_workout

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.gym_workout.database.DatabaseHelper2
import com.example.gym_workout.databinding.ActivityMain1Binding

class MainActivity1 : AppCompatActivity() {

    private lateinit var binding: ActivityMain1Binding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val dbHelper = DatabaseHelper2(this)
        dbHelper.readableDatabase
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        binding = ActivityMain1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home(), "Home")

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Home(), "Home")
                R.id.profile -> replaceFragment(Profile(), "Profile")
                R.id.run -> replaceFragment(Activity(), "Activity")
                R.id.food -> replaceFragment(Food(), "Food")
                else -> {

                }
            }
            true
        }

        val settingsIcon: ImageView = findViewById(R.id.setting_icon)
        settingsIcon.setOnClickListener {
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
        updateHeaderText(title)
    }

    private fun updateHeaderText(title: String) {
        val headerTitle = findViewById<TextView>(R.id.headerTitle)
        headerTitle.text = title
    }
}
