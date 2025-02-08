package com.example.gym_workout

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.gym_workout.database.DatabaseHelper2
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class LocationAndStepService : Service(), SensorEventListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private lateinit var databaseHelper: DatabaseHelper2

    override fun onCreate() {
        super.onCreate()
        databaseHelper = DatabaseHelper2(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)
        } else {
            Toast.makeText(this, "Step Counter sensor not available!", Toast.LENGTH_SHORT).show()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity1::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE // Ensure the pending intent is immutable
        )

        val notification: Notification = NotificationCompat.Builder(this, "LocationServiceChannel")
            .setContentTitle("Gym Workout")
            .setContentText("Tracking your steps and location")
            .setSmallIcon(R.drawable.flame)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    private fun setupLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    saveRouteCoordinate(currentLatLng)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun saveRouteCoordinate(latLng: LatLng) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        databaseHelper.saveRouteCoordinate(currentDate, latLng.latitude, latLng.longitude)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            if (previousTotalSteps == 0f) {
                previousTotalSteps = event.values[0]
            }
            totalSteps = event.values[0]
            val currentSteps = (totalSteps - previousTotalSteps).toInt()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            databaseHelper.saveStepsCount(currentDate, currentSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "LocationServiceChannel",
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
