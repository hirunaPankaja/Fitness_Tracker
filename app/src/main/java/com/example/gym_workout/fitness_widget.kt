package com.example.gym_workout

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.RemoteViews
import com.example.gym_workout.database.DatabaseHelper2
import com.example.gym_workout.utils.DonutProgressView
import java.text.SimpleDateFormat
import java.util.*

import android.os.Handler
import android.os.Looper

class fitness_widget : AppWidgetProvider() {
    private lateinit var databaseHelper: DatabaseHelper2
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        startPeriodicUpdates(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        databaseHelper = DatabaseHelper2(context)
    }

    override fun onDisabled(context: Context) {
        stopPeriodicUpdates()
    }

    private fun startPeriodicUpdates(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateRunnable = object : Runnable {
            override fun run() {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
                handler.postDelayed(this, 10000) // 10 seconds
            }
        }
        handler.post(updateRunnable)
    }

    private fun stopPeriodicUpdates() {
        handler.removeCallbacks(updateRunnable)
    }

    fun getWidgetBitmap(context: Context, steps: Float, calories: Float): Bitmap {
        val view = DonutProgressView(context, null)
        view.setProgress(steps, calories)

        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, 200, 200)
        view.draw(canvas)

        return bitmap
    }

    companion object {
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.fitness_widget)
            val databaseHelper = DatabaseHelper2(context)

            // Get the current date
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Fetch the steps and calories count for the current date
            val (stepsCount, caloriesCount) = databaseHelper.getStepsAndCaloriesForDate(currentDate)

            // Calculate the percentage of steps completed
            val stepsPercentage = (stepsCount.toFloat() / 6000) * 100
            val caloriesPercentage = (caloriesCount.toFloat() / 300) * 100

            // Generate bitmap with updated progress
            val bitmap = fitness_widget().getWidgetBitmap(context, stepsPercentage, caloriesPercentage)

            // Set the bitmap to ImageView inside the widget
            views.setImageViewBitmap(R.id.widget_image, bitmap)

            // Update steps label and text
            views.setTextViewText(R.id.steps_label, "Steps")
            views.setTextViewText(R.id.steps_text, "$stepsCount / 6000")

            // Update calories label and text
            views.setTextViewText(R.id.calories_label, "Calories")
            views.setTextViewText(R.id.calories_text, "$caloriesCount / 300")

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
