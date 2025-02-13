package com.example.gym_workout

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.work.impl.utils.workForeground
import com.example.gym_workout.utils.DonutProgressView

class fitness_widget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {}

    override fun onDisabled(context: Context) {}

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

            // Generate bitmap with updated progress
            val bitmap = fitness_widget().getWidgetBitmap(context, 3000f / 6000f * 100, 150f / 300f * 100) // Example values

            // Set the bitmap to ImageView inside the widget
            views.setImageViewBitmap(R.id.widget_image, bitmap)

            // Update steps label and text
            views.setTextViewText(R.id.steps_label, "Steps")
            views.setTextViewText(R.id.steps_text, "300 / 6000")

            // Update calories label and text
            views.setTextViewText(R.id.calories_label, "Calories")
            views.setTextViewText(R.id.calories_text, "150 / 300")

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}
