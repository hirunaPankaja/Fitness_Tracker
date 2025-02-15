package com.example.gym_workout.utils

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_workout.LowerBodyWorkoutActivity
import com.example.gym_workout.R
import com.example.gym_workout.UpperBodyWorkoutActivity
import com.example.gym_workout.YogaActivity
import com.example.gym_workout.fullbodyworkout_activity

class SessionAdapter(private val sessionItems: MutableList<SessionItem>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    fun updateData(newSessionItems: List<SessionItem>) {
        sessionItems.clear()
        sessionItems.addAll(newSessionItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_item, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val item = sessionItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return sessionItems.size
    }

    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sessionName: TextView = itemView.findViewById(R.id.itemTitle)
        private val sessionDuration: TextView = itemView.findViewById(R.id.itemSubtitle)
        private val sessionIcon: ImageView = itemView.findViewById(R.id.itemImage)

        fun bind(session: SessionItem) {
            sessionName.text = session.title
            sessionDuration.text = session.subtitle
            sessionIcon.setImageResource(session.imageResId)

            itemView.setOnClickListener {
                when (session.title) {
                    "Upper Body" -> {
                        val intent = Intent(itemView.context, UpperBodyWorkoutActivity::class.java)
                        itemView.context.startActivity(intent)
                    }
                    "Lower Body" -> {
                        val intent = Intent(itemView.context, LowerBodyWorkoutActivity::class.java)
                        itemView.context.startActivity(intent)
                    }
                    "Yoga" -> {
                        val intent = Intent(itemView.context, YogaActivity::class.java)
                        itemView.context.startActivity(intent)
                    }
                    "Full Body" -> {
                        val intent = Intent(itemView.context, fullbodyworkout_activity::class.java)
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }
}
