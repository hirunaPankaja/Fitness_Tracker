package com.example.gym_workout.utils

import java.util.Calendar
import java.util.Locale

class DateUtils {

    fun generateDates(month: Int, year: Int): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDay) {
            dates.add("$year-${month + 1}-$day")
        }
        return dates
    }
}