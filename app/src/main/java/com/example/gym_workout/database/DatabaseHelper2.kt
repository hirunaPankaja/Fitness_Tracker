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
        const val COLUMN_CALORIES = "calories"
        const val COLUMN_DATE = "date"

        const val TABLE_ROUTE = "route_data"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper2", "Creating database tables")

        val createActivityTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_ACTIVITY (
                $COLUMN_DATE DATE PRIMARY KEY,
                $COLUMN_STEPS_COUNT INTEGER DEFAULT 0,
                $COLUMN_CALORIES INTEGER DEFAULT 0
            )
        """
        db.execSQL(createActivityTableQuery)

        val createRouteTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_ROUTE (
                $COLUMN_DATE DATE,
                $COLUMN_LATITUDE REAL,
                $COLUMN_LONGITUDE REAL
            )
        """
        db.execSQL(createRouteTableQuery)
        Log.d("DatabaseHelper2", "Tables created successfully")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper2", "Upgrading database from version $oldVersion to $newVersion")
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_ACTIVITY ADD COLUMN $COLUMN_CALORIES INTEGER DEFAULT 0")
        }
    }

    // Method to save steps count
    fun saveStepsCount(date: String, steps: Int): Boolean {
        val db = writableDatabase
        return try {
            ensureDateRecordExists(db, date)
            val values = ContentValues().apply {
                put(COLUMN_STEPS_COUNT, steps)
            }
            db.update(TABLE_ACTIVITY, values, "$COLUMN_DATE = ?", arrayOf(date)) > 0
        } catch (e: Exception) {
            Log.e("DatabaseHelper2", "Error saving steps: ${e.message}")
            false
        } finally {
            db.close()
        }
    }


    // Method to save calories (only updates the calories column)
    fun saveCalories(date: String, calories: Int): Boolean {
        val db = writableDatabase
        return try {
            ensureDateRecordExists(db, date) // Ensure the record exists
            val values = ContentValues().apply {
                put(COLUMN_CALORIES, calories)
            }
            // Update only the calories column for the given date
            db.update(TABLE_ACTIVITY, values, "$COLUMN_DATE = ?", arrayOf(date)) > 0
        } catch (e: Exception) {
            Log.e("DatabaseHelper2", "Error saving calories: ${e.message}")
            false
        } finally {
            db.close()
        }
    }
    // Helper method to ensure a record exists for the given date
    private fun ensureDateRecordExists(db: SQLiteDatabase, date: String) {
        val cursor = db.query(
            TABLE_ACTIVITY,
            arrayOf(COLUMN_DATE),
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null, null, null
        )

        if (!cursor.moveToFirst()) {
            // Record doesn't exist, create it with default values
            val values = ContentValues().apply {
                put(COLUMN_DATE, date)
                put(COLUMN_STEPS_COUNT, 0)
                put(COLUMN_CALORIES, 0)
            }
            db.insert(TABLE_ACTIVITY, null, values)
        }
        cursor.close()
    }

    // Method to get steps and calories for a specific date
    fun getStepsAndCaloriesForDate(date: String): Pair<Int, Int> {
        val db = readableDatabase
        return try {
            val cursor = db.query(
                TABLE_ACTIVITY,
                arrayOf(COLUMN_STEPS_COUNT, COLUMN_CALORIES),
                "$COLUMN_DATE = ?",
                arrayOf(date),
                null, null, null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    Pair(
                        it.getInt(it.getColumnIndexOrThrow(COLUMN_STEPS_COUNT)),
                        it.getInt(it.getColumnIndexOrThrow(COLUMN_CALORIES))
                    )
                } else {
                    Pair(0, 0)
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper2", "Error getting steps and calories: ${e.message}")
            Pair(0, 0)
        } finally {
            db.close()
        }
    }

    // Method to save route coordinates
    fun saveRouteCoordinate(date: String, latitude: Double, longitude: Double): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
        }
        return db.insert(TABLE_ROUTE, null, contentValues) != -1L
    }

    // Method to get route coordinates for a specific date
    fun getRouteForDate(date: String): List<LatLng> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ROUTE,
            arrayOf(COLUMN_LATITUDE, COLUMN_LONGITUDE),
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null, null, null
        )

        val route = mutableListOf<LatLng>()
        while (cursor.moveToNext()) {
            val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))
            val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
            route.add(LatLng(latitude, longitude))
        }
        cursor.close()
        return route
    }

    // Method to clear data from the previous month
    fun clearPreviousMonthData(): Boolean {
        val db = writableDatabase
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val firstDayOfMonth = currentDate.substring(0, 8) + "01"

        val activityDeleted = db.delete(TABLE_ACTIVITY, "$COLUMN_DATE < ?", arrayOf(firstDayOfMonth)) > 0
        val routeDeleted = db.delete(TABLE_ROUTE, "$COLUMN_DATE < ?", arrayOf(firstDayOfMonth)) > 0

        db.close()
        return activityDeleted || routeDeleted
    }
}