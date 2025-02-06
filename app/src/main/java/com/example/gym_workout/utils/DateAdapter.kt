package com.example.gym_workout.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_workout.R
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter(private val dates: List<String>, private val listener: OnDateClickListener) :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = -1
    private var currentDatePosition = -1

    interface OnDateClickListener {
        fun onDateClick(date: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val originalDate = dates[position]
        holder.bind(originalDate)

        val displayDate = formatDateForDisplay(originalDate)
        val currentDate = getCurrentDateForDisplay()

        val context = holder.dateTextView.context

        when {
            displayDate == currentDate -> {
                currentDatePosition = holder.adapterPosition
                holder.dateTextView.background = ContextCompat.getDrawable(context, R.drawable.date_background_highlighted)
            }
            position == selectedPosition -> {
                holder.dateTextView.background = ContextCompat.getDrawable(context, R.drawable.date_background_highlighted)
            }
            displayDate <= currentDate -> {
                // Highlight previous dates the same as the current date
                holder.dateTextView.background = ContextCompat.getDrawable(context, R.drawable.previous_date_background)
            }
            else -> {
                holder.dateTextView.background = ContextCompat.getDrawable(context, R.drawable.date_background)
            }
        }

        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            listener.onDateClick(originalDate)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = dates.size

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bind(date: String) {
            dateTextView.text = formatDateForDisplay(date)
        }
    }

    private fun formatDateForDisplay(date: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = sdf.parse(date)
            val displaySdf = SimpleDateFormat("d MMM", Locale.getDefault())
            displaySdf.format(parsedDate)
        } catch (e: Exception) {
            e.printStackTrace()
            date
        }
    }

    private fun getCurrentDateForDisplay(): String {
        return try {
            val sdf = SimpleDateFormat("d MMM", Locale.getDefault())
            sdf.format(Date())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun highlightCurrentDate(position: Int) {
        currentDatePosition = position
        notifyDataSetChanged()
    }

    fun slightlyHighlightPreviousDate(position: Int) {
        // Logic for slightly highlighting previous dates can be added here
        notifyDataSetChanged()
    }

    fun updateDates(newDates: List<String>) {
        // Logic for updating dates in the adapter can be added here
    }
}
