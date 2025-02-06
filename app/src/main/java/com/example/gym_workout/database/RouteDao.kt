package com.example.gym_workout.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RouteDao {
    @Insert
    fun insert(route: Route)

    @Query("DELETE FROM route WHERE timestamp < :timeLimit")
    fun deleteOldRoutes(timeLimit: Long)

    @Query("SELECT * FROM route")
    fun getAllRoutes(): List<Route>
}
