package com.example.gym_workout.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_workout.R

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
        holder.itemTitle.text = item.title
        holder.itemSubtitle.text = item.subtitle
        holder.itemImage.setImageResource(item.imageResId)
    }

    override fun getItemCount(): Int {
        return sessionItems.size
    }

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemTitle: TextView = itemView.findViewById(R.id.itemTitle)
        val itemSubtitle: TextView = itemView.findViewById(R.id.itemSubtitle)
    }
}