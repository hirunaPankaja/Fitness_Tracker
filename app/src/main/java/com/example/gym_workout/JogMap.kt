package com.example.gym_workout

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.gym_workout.database.DataManager
import com.example.gym_workout.database.DatabaseHelper2
import com.google.android.gms.maps.model.PolylineOptions
import java.text.SimpleDateFormat
import java.util.*


class JogMap : AppCompatActivity(), OnMapReadyCallback {

    // Class-level variables
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val routePoints = ArrayList<LatLng>()

    private lateinit var txtTime: TextView
    private lateinit var txtDistance: TextView
    private lateinit var txtCalories: TextView
    private lateinit var btnStart: Button

    private var isRunning = false
    private var startTime: Long = 0
    private val timerHandler = Handler()
    private var totalDistance = 0.0
    private var lastLocation: LatLng? = null
    private lateinit var dataManager: DataManager
    private var userWeight: Float? = null
    private lateinit var dbHelper: DatabaseHelper2
    private var currentCalories = 0.0
    private lateinit var today: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jog_map)

        // Initialize today with the current date
        today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Initialize views
        txtTime = findViewById(R.id.txtTime)
        txtDistance = findViewById(R.id.txtDistance)
        txtCalories = findViewById(R.id.txtCalories)
        btnStart = findViewById(R.id.btnStart)
        dbHelper = DatabaseHelper2(this)

        // Fetch existing calories for today
        val (_, existingCalories) = dbHelper.getStepsAndCaloriesForDate(today)
        currentCalories = existingCalories.toDouble()

        // Initialize map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize data manager and fetch user weight
        dataManager = DataManager(this)
        userWeight = dataManager.getUserWeight("user1")

        // Set button click listener
        btnStart.setOnClickListener {
            if (!isRunning) {
                startCountdown()
            } else {
                stopRun()
            }
        }
    }

    private fun startCountdown() {
        btnStart.text = "3"
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                btnStart.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                startRun()
            }
        }.start()
    }

    private fun startRun() {
        isRunning = true
        btnStart.text = "STOP"
        startTime = System.currentTimeMillis()
        totalDistance = 0.0
        lastLocation = null

        timerHandler.post(runTimer)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    if (!isRunning) return

                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    if (lastLocation != null) {
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            lastLocation!!.latitude, lastLocation!!.longitude,
                            currentLatLng.latitude, currentLatLng.longitude,
                            results
                        )
                        totalDistance += results[0] / 1000.0
                        txtDistance.text = String.format(Locale.getDefault(), "Distance: %.2f km", totalDistance)
                    }

                    lastLocation = currentLatLng
                    routePoints.add(currentLatLng)
                    map.addPolyline(PolylineOptions().addAll(routePoints).width(5f))

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permissions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopRun() {
        isRunning = false
        btnStart.text = "START"
        timerHandler.removeCallbacks(runTimer)
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Calculate calories burned
        val caloriesBurned = userWeight?.let { weight -> 0.75 * weight * totalDistance } ?: 0.0

        // Calculate total calories
        val totalCalories = currentCalories + caloriesBurned

        // Save to database
        saveCaloriesToDatabase(today, totalCalories.toInt())
    }

    private val runTimer = object : Runnable {
        override fun run() {
            val elapsed = (System.currentTimeMillis() - startTime) / 1000
            txtTime.text = String.format(Locale.getDefault(), "Time: %02d:%02d:%02d", elapsed / 3600, (elapsed % 3600) / 60, elapsed % 60)

            // Calculate calories burned
            val caloriesBurned = userWeight?.let { weight -> 0.75 * weight * totalDistance } ?: 0.0
            txtCalories.text = String.format(Locale.getDefault(), "Calories: %.1f kcal", caloriesBurned)

            // Schedule the next update
            timerHandler.postDelayed(this, 1000)
        }
    }

    private fun saveCaloriesToDatabase(date: String, totalCalories: Int) {
        try {
            dbHelper.saveCalories(date, totalCalories)
        } catch (e: Exception) {
            Log.e("JogMap", "Error saving calories: ${e.message}")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            enableLocationTracking()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableLocationTracking()
            } else {
                // Permission denied, handle appropriately
            }
        }
    }

    private fun enableLocationTracking() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permissions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        map.isMyLocationEnabled = true

        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    if (!isRunning) return

                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    if (lastLocation != null) {
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            lastLocation!!.latitude, lastLocation!!.longitude,
                            currentLatLng.latitude, currentLatLng.longitude,
                            results
                        )
                        totalDistance += results[0] / 1000.0
                        txtDistance.text = String.format(Locale.getDefault(), "Distance: %.2f km", totalDistance)
                    }

                    lastLocation = currentLatLng
                    routePoints.add(currentLatLng)
                    map.addPolyline(PolylineOptions().addAll(routePoints).width(5f))

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
}