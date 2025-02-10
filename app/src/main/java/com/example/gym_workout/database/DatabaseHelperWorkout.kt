package com.example.gym_workout.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class DatabaseHelperWorkout(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "gym_workout.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_EXERCISES = "exercises"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_TITLE = "title"
        const val COLUMN_INSTRUCTIONS = "instructions"
        const val COLUMN_REPS_SETS = "repsSets"
        const val COLUMN_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_EXERCISES ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_TITLE TEXT, $COLUMN_INSTRUCTIONS TEXT, $COLUMN_REPS_SETS TEXT, $COLUMN_IMAGE BLOB)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISES")
        onCreate(db)
    }

    fun insertExercise(name: String, title: String, instructions: String, repsSets: String, bitmap: Bitmap) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_TITLE, title)
            put(COLUMN_INSTRUCTIONS, instructions)
            put(COLUMN_REPS_SETS, repsSets)
            put(COLUMN_IMAGE, bitmapToByteArray(bitmap))
        }

        db.insert(TABLE_EXERCISES, null, values)
        db.close()
    }

    fun getExercise(name: String): Exercise? {
        val db = readableDatabase

        val cursor: Cursor = db.query(
            TABLE_EXERCISES,
            arrayOf(COLUMN_TITLE, COLUMN_INSTRUCTIONS, COLUMN_REPS_SETS, COLUMN_IMAGE),
            "$COLUMN_NAME = ?",
            arrayOf(name),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
            val instructionsIndex = cursor.getColumnIndex(COLUMN_INSTRUCTIONS)
            val repsSetsIndex = cursor.getColumnIndex(COLUMN_REPS_SETS)
            val imageIndex = cursor.getColumnIndex(COLUMN_IMAGE)

            if (titleIndex != -1 && instructionsIndex != -1 && repsSetsIndex != -1 && imageIndex != -1) {
                val title = cursor.getString(titleIndex)
                val instructions = cursor.getString(instructionsIndex)
                val repsSets = cursor.getString(repsSetsIndex)
                val imageBytes = cursor.getBlob(imageIndex)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                cursor.close()
                db.close()
                Exercise(name, title, instructions, repsSets, bitmap)
            } else {
                cursor.close()
                db.close()
                null
            }
        } else {
            cursor.close()
            db.close()
            null
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    data class Exercise(
        val name: String,
        val title: String,
        val instructions: String,
        val repsSets: String,
        val image: Bitmap
    )
}
