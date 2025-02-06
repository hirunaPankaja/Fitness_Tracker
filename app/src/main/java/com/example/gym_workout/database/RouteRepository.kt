package com.example.gym_workout.database

import android.content.Context
import androidx.room.Room

class RouteRepository(context: Context) {
    private val db = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "route_db").build()
    private val routeDao = db.routeDao()

    fun insert(route: Route) {
        Thread {
            routeDao.insert(route)
        }.start()
    }

    fun deleteOldRoutes(timeLimit: Long) {
        Thread {
            routeDao.deleteOldRoutes(timeLimit)
        }.start()
    }

    fun getAllRoutes(callback: (List<Route>) -> Unit) {
        Thread {
            val routes = routeDao.getAllRoutes()
            callback(routes)
        }.start()
    }
}
