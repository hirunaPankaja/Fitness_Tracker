package com.example.gym_workout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_workout.R
import com.example.gym_workout.utils.DateAdapter
import com.example.gym_workout.utils.DateUtils
import java.util.*

class MainActivity : AppCompatActivity(), DateAdapter.OnDateClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dateRecyclerView: RecyclerView = findViewById(R.id.dateRecyclerView)
        dateRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val dates = DateUtils().generateDates(month, year)
        val adapter = DateAdapter(dates, this)
        dateRecyclerView.adapter = adapter
    }

    override fun onDateClick(date: String) {
        val dateInformation = retrieveDateInformation(date)
    }

    private fun retrieveDateInformation(date: String): DateUtils {
        return DateUtils()
    }
}
