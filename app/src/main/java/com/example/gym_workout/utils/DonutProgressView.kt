package com.example.gym_workout.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class DonutProgressView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val strokeWidth = 10f
    private val circleGap = 20f
    private val iconSize = 20f

    private val filledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@DonutProgressView.strokeWidth
        color = Color.LTGRAY
        strokeCap = Paint.Cap.ROUND // Rounded ends for non-highlighted sections
    }

    private val highlightPaintSteps = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@DonutProgressView.strokeWidth
        color = Color.parseColor("#FF8000")
        strokeCap = Paint.Cap.ROUND // Rounded ends for highlighted steps
    }

    private val highlightPaintCalories = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@DonutProgressView.strokeWidth
        color = Color.parseColor("#00FF00")
        strokeCap = Paint.Cap.ROUND // Rounded ends for highlighted calories
    }

    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = this@DonutProgressView.iconSize
    }

    private var stepProgress = 0f
    private var calorieProgress = 0f
    private val stepGoal = 6000
    private val calorieGoal = 300

    // Preallocate RectF objects
    private val outerCircle = RectF()
    private val innerCircle = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        // Set the dimensions to fit within 200dp width and 150dp height
        setMeasuredDimension(200, 150)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = 150f // Fixed width
        val height = 150f // Fixed height

        // Set RectF dimensions based on fixed width and height
        outerCircle.set(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth)
        innerCircle.set(circleGap + strokeWidth, circleGap + strokeWidth, width - (circleGap + strokeWidth), height - (circleGap + strokeWidth))

        // Define the gap angle (e.g., 30 degrees)
        val gapAngle = 30f
        val startAngle = -80f + gapAngle / 2

        // Draw full circles with gaps
        canvas.drawArc(outerCircle, startAngle, 345f - gapAngle, false, filledPaint)
        canvas.drawArc(innerCircle, startAngle, 345f - gapAngle, false, filledPaint)

        // Draw progress arcs with gaps
        val stepSweepAngle = stepProgress * 3.6f - gapAngle
        val calorieSweepAngle = calorieProgress * 3.6f - gapAngle
        canvas.drawArc(outerCircle, startAngle, stepSweepAngle, false, highlightPaintSteps)
        canvas.drawArc(innerCircle, startAngle, calorieSweepAngle, false, highlightPaintCalories)

        // Draw icons at the top of the gaps
        val iconX = width / 2 - iconSize / 2
        val stepIconY = strokeWidth + iconSize - 20f
        val calorieIconY = circleGap + strokeWidth + iconSize - 17f
        canvas.drawText("👟", iconX, stepIconY, iconPaint) // Shoe icon for steps
        canvas.drawText("🔥", iconX, calorieIconY, iconPaint) // Flame icon for calories
    }

    fun setProgress(steps: Float, calories: Float) {
        this.stepProgress = steps
        this.calorieProgress = calories
        invalidate()
    }
}