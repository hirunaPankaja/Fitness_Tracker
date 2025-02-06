package com.example.gym_workout

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlin.math.sqrt

class Progress : AppCompatActivity(), SensorEventListener, LocationListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager
    private var stepCounter: Sensor? = null
    private var stepDetector: Sensor? = null
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var detectedSteps = 0
    private var lastStepTime: Long = 0
    private lateinit var tvStepTaken: TextView
    private lateinit var stepProgressCircle: CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)
        setContentView(R.layout.fragment_activity)

        tvStepTaken = findViewById(R.id.tv_stepTaken)
        stepProgressCircle = findViewById(R.id.stepProgressCircle)

        loadData()
        resetSteps()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        requestPermissions()
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    override fun onResume() {
        super.onResume()
        running = true

        stepCounter?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        stepDetector?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST) }
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
    }

    private fun lowPass(input: FloatArray, output: FloatArray?): FloatArray {
        val alpha = 0.8f
        if (output == null) return input

        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
        return output
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !running) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                totalSteps = event.values[0]
                val currentSteps = (totalSteps - previousTotalSteps).toInt()
                if (currentSteps >= 0) updateStepCount(currentSteps)
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastStepTime > 500) { // 500ms threshold
                    detectedSteps++
                    lastStepTime = currentTime
                    updateStepCount(detectedSteps)
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val filteredValues = lowPass(event.values.clone(), null)
                val acceleration = sqrt(
                    filteredValues[0] * filteredValues[0] +
                            filteredValues[1] * filteredValues[1] +
                            filteredValues[2] * filteredValues[2]
                )
                if (acceleration > 12) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastStepTime > 500) { // 500ms threshold
                        detectedSteps++
                        lastStepTime = currentTime
                        updateStepCount(detectedSteps)
                    }
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                // Additional logic using gyroscope data if needed
            }
        }
    }

    private fun updateStepCount(steps: Int) {
        tvStepTaken.text = steps.toString()
        stepProgressCircle.setProgressWithAnimation(steps.toFloat())
    }

    private fun resetSteps() {
        tvStepTaken.setOnLongClickListener {
            previousTotalSteps = totalSteps
            detectedSteps = 0
            saveData()
            tvStepTaken.text = "0"
            stepProgressCircle.setProgressWithAnimation(0f)
            true
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        previousTotalSteps = sharedPreferences.getFloat("key1", 0f)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onLocationChanged(location: Location) {
        // Handle location updates if needed
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}
