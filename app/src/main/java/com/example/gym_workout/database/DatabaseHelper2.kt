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
        const val DATABASE_VERSION = 2 // Incremented version to trigger onCreate

        const val TABLE_ACTIVITY = "activity_data"
        const val COLUMN_STEPS_COUNT = "steps_count"
        const val COLUMN_DATE = "date"

        const val TABLE_ROUTE = "route_data"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper2", "Creating database tables")

        val createActivityTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_ACTIVITY (
                $COLUMN_STEPS_COUNT INTEGER,
                $COLUMN_DATE DATE
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
        // Handle database upgrade if needed
    }

    /**
     * Ensure tables are created if they do not exist
     */
    private fun ensureTablesExist(db: SQLiteDatabase) {
        val createActivityTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_ACTIVITY (
                $COLUMN_STEPS_COUNT INTEGER,
                $COLUMN_DATE DATE
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
    }

    fun saveStepsCount(date: String, stepsCount: Int): Boolean {
        val db = writableDatabase
        ensureTablesExist(db)
        val contentValues = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_STEPS_COUNT, stepsCount)
        }

        val cursor = db.query(
            TABLE_ACTIVITY,
            arrayOf(COLUMN_DATE),
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null, null, null
        )

        val rowExists = cursor.moveToFirst()
        cursor.close()

        return if (rowExists) {
            db.update(TABLE_ACTIVITY, contentValues, "$COLUMN_DATE = ?", arrayOf(date)) > 0
        } else {
            db.insert(TABLE_ACTIVITY, null, contentValues) != -1L
        }.also {
            db.close()
        }
    }

    fun saveRouteCoordinate(date: String, latitude: Double, longitude: Double): Boolean {
        val db = writableDatabase
        ensureTablesExist(db)
        val contentValues = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
        }

        return db.insert(TABLE_ROUTE, null, contentValues) != -1L
    }

    fun getStepsCountForDate(date: String): Int {
        val db = readableDatabase
        ensureTablesExist(db)
        val cursor = db.query(
            TABLE_ACTIVITY,
            arrayOf(COLUMN_STEPS_COUNT),
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null, null, null
        )

        var stepsCount = 0
        if (cursor.moveToFirst()) {
            stepsCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STEPS_COUNT))
        } else {
            Log.e("DatabaseError", "No data found for the date: $date")
        }
        cursor.close()
        return stepsCount
    }

    fun getRouteForDate(date: String): List<LatLng> {
        val db = readableDatabase
        ensureTablesExist(db)
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

    fun clearPreviousMonthData(): Boolean {
        val db = writableDatabase
        ensureTablesExist(db)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val firstDayOfMonth = currentDate.substring(0, 8) + "01"

        val activityDeleted = db.delete(TABLE_ACTIVITY, "$COLUMN_DATE < ?", arrayOf(firstDayOfMonth)) > 0
        val routeDeleted = db.delete(TABLE_ROUTE, "$COLUMN_DATE < ?", arrayOf(firstDayOfMonth)) > 0

        db.close()
        return activityDeleted || routeDeleted
    }
}
