package com.example.gym_workout.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper2(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "fitness_tracker.db"
        const val DATABASE_VERSION = 2

        const val TABLE_ACTIVITY = "activity_data"
        const val COLUMN_STEPS_COUNT = "steps_count"
        const val COLUMN_DATE = "date"

        const val TABLE_ROUTE = "route_data"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_ACTIVITY (
                $COLUMN_STEPS_COUNT INTEGER,
                $COLUMN_DATE DATE
            )
        """
        db.execSQL(createTableQuery)

        val createRouteTableQuery = """
            CREATE TABLE $TABLE_ROUTE (
                $COLUMN_DATE DATE,
                $COLUMN_LATITUDE REAL,
                $COLUMN_LONGITUDE REAL
            )
        """
        db.execSQL(createRouteTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }

    fun saveStepsCount(date: String, stepsCount: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_STEPS_COUNT, stepsCount)
        }

        val rowsAffected = db.update(TABLE_ACTIVITY, contentValues, "$COLUMN_DATE=?", arrayOf(date))
        if (rowsAffected == 0) {
            db.insert(TABLE_ACTIVITY, null, contentValues)
        }
    }

    fun getStepsCountForDate(date: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_ACTIVITY, arrayOf(COLUMN_STEPS_COUNT), "$COLUMN_DATE=?", arrayOf(date), null, null, null)
        var stepsCount = 0
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_STEPS_COUNT)
            if (columnIndex != -1) {
                stepsCount = cursor.getInt(columnIndex)
            } else {
                Log.e("DatabaseError", "Column $COLUMN_STEPS_COUNT not found")
            }
            cursor.close()
        }
        return stepsCount
    }

    fun saveRouteCoordinate(date: String, latitude: Double, longitude: Double) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
        }
        db.insert(TABLE_ROUTE, null, contentValues)
    }

    fun getRouteForDate(date: String): List<LatLng> {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_ROUTE, arrayOf(COLUMN_LATITUDE, COLUMN_LONGITUDE), "$COLUMN_DATE=?", arrayOf(date), null, null, null)
        val route = mutableListOf<LatLng>()

        cursor.use {
            val latitudeIndex = it.getColumnIndexOrThrow(COLUMN_LATITUDE)
            val longitudeIndex = it.getColumnIndexOrThrow(COLUMN_LONGITUDE)

            while (it.moveToNext()) {
                val latitude = it.getDouble(latitudeIndex)
                val longitude = it.getDouble(longitudeIndex)
                route.add(LatLng(latitude, longitude))
            }
        }

        return route
    }

    fun clearPreviousMonthData() {
        val db = this.writableDatabase
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val firstDayOfMonth = currentDate.substring(0, 8) + "01"

        db.delete(TABLE_ACTIVITY, "$COLUMN_DATE < ?", arrayOf(firstDayOfMonth))
        db.delete(TABLE_ROUTE, "$COLUMN_DATE < ?", arrayOf(firstDayOfMonth))
    }
}
