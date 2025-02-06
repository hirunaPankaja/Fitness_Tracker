package com.example.gym_workout.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route")
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
