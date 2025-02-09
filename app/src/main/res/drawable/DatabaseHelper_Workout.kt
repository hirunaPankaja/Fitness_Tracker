package com.example.gym_workout.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.ContentValues
import java.io.ByteArrayOutputStream

class DatabaseHelper_Workout(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "workout.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_EXERCISES = "exercises"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_INSTRUCTIONS = "instructions"
        private const val COLUMN_REPS_SETS = "reps_sets"
        private const val COLUMN_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_EXERCISES_TABLE = ("CREATE TABLE " + TABLE_EXERCISES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_INSTRUCTIONS + " TEXT,"
                + COLUMN_REPS_SETS + " TEXT,"
                + COLUMN_IMAGE + " BLOB" + ")")
        db.execSQL(CREATE_EXERCISES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES)
        onCreate(db)
    }

    fun insertExercise(name: String, title: String, instructions: String, repsSets: String, image: Bitmap) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_INSTRUCTIONS, instructions)
        values.put(COLUMN_REPS_SETS, repsSets)

        // Convert Bitmap to byte array
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        values.put(COLUMN_IMAGE, byteArray)

        db.insert(TABLE_EXERCISES, null, values)
        db.close()
    }

    fun getExercise(name: String): Exercise? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_EXERCISES,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_TITLE, COLUMN_INSTRUCTIONS, COLUMN_REPS_SETS, COLUMN_IMAGE),
            "$COLUMN_NAME=?",
            arrayOf(name),
            null,
            null,
            null,
            null
        )
        cursor?.let {
            if (it.moveToFirst()) {
                val exercise = Exercise(
                    it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_REPS_SETS)),
                    BitmapFactory.decodeByteArray(it.getBlob(it.getColumnIndexOrThrow(COLUMN_IMAGE)), 0, it.getBlob(it.getColumnIndexOrThrow(COLUMN_IMAGE)).size)
                )
                it.close()
                db.close()
                return exercise
            }
            it.close()
        }
        db.close()
        return null
    }
}

data class Exercise(
    val name: String,
    val title: String,
    val instructions: String,
    val repsSets: String,
    val image: Bitmap
)
