package com.example.gym_workout

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_workout.database.DatabaseHelper2
import com.example.gym_workout.utils.DateAdapter
import com.example.gym_workout.utils.DateUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.text.SimpleDateFormat
import java.util.*

class Activity : Fragment(), DateAdapter.OnDateClickListener, OnMapReadyCallback, SensorEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mapPanel: LinearLayout
    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private lateinit var footStepsTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var distanceText: TextView
    private lateinit var serviceManager: ServiceManager
    private lateinit var switchLocation: Switch
    private lateinit var switchSteps: Switch
    private lateinit var stepsIndicator: View
    private lateinit var gpsSignalIndicator: View
    private var currentCalories = 0

    private lateinit var polylineOptions: PolylineOptions
    private lateinit var currentPolyline: Polyline

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var databaseHelper: DatabaseHelper2
    private lateinit var adapter: DateAdapter
    private var isViewingHistory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper2(requireContext())
        serviceManager = ServiceManager(requireContext())
        arguments?.let {
            // Additional initialization code if needed
        }
        requestPermissions()
        renewDataAtMidnight()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_activity, container, false)

        // Initialize views
        switchLocation = view.findViewById(R.id.switchLocation)
        switchSteps = view.findViewById(R.id.switchSteps)
        stepsIndicator = view.findViewById(R.id.stepsBeepDot)
        gpsSignalIndicator = view.findViewById(R.id.gpsSignalIndicator)
        footStepsTextView = view.findViewById(R.id.footSteps)
        caloriesTextView = view.findViewById(R.id.calories_value)
        distanceText = view.findViewById(R.id.distanceText)

        // Restore switch states
        switchSteps.isChecked = serviceManager.isStepsServiceEnabled()
        switchLocation.isChecked = serviceManager.isLocationServiceEnabled()

        // Restore indicator states
        serviceManager.restoreState(stepsIndicator, gpsSignalIndicator)

        // Set up switch listeners
        switchSteps.setOnCheckedChangeListener { _, isChecked ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                serviceManager.toggleStepsService(isChecked, stepsIndicator)
            }
        }

        switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                serviceManager.toggleLocationService(isChecked, gpsSignalIndicator)
            }
        }

        val dateRecyclerView: RecyclerView = view.findViewById(R.id.dateRecyclerView)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        dateRecyclerView.layoutManager = linearLayoutManager

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val dates = DateUtils().generateDates(month, year)
        adapter = DateAdapter(dates, this)
        dateRecyclerView.adapter = adapter

        // Auto-select current date
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        Handler(Looper.getMainLooper()).postDelayed({
            loadDataForDate(currentDate)
        }, 500)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapPanel = view.findViewById(R.id.mapPanel)
        mapPanel.setOnClickListener {
            val intent = Intent(context, Map1::class.java)
            startActivity(intent)
        }

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounter == null) {
            Toast.makeText(context, "Step Counter sensor not available!", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupLocationCallback()

        return view
    }

    private fun calculateCaloriesFromSteps(steps: Int): Int {
        val caloriesPerStep = 0.04
        return (steps * caloriesPerStep).toInt()
    }

    private fun calculateCaloriesFromDistance(distanceKm: Double): Int {
        val caloriesPerKm = 60
        return (distanceKm * caloriesPerKm).toInt()
    }

    private fun updateCaloriesDisplay(calories: Int) {
        caloriesTextView.text = "$calories kcal"
    }

    private fun calculateAndUpdateCalories(steps: Int, route: List<LatLng>) {
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            totalDistance += calculateDistance(route[i], route[i + 1])
        }

        val caloriesFromSteps = calculateCaloriesFromSteps(steps)
        val caloriesFromDistance = calculateCaloriesFromDistance(totalDistance)

        val totalCalories = maxOf(caloriesFromSteps, caloriesFromDistance)

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        databaseHelper.saveCalories(currentDate, totalCalories)
        updateCaloriesDisplay(totalCalories)
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!isViewingHistory && serviceManager.isLocationServiceEnabled()) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        val points = currentPolyline.points
                        points.add(currentLatLng)
                        currentPolyline.points = points

                        saveRouteCoordinate(currentLatLng)
                        updateDistanceDisplay(points)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f))
                    }
                }
            }
        }
    }

    private fun loadDataForDate(date: String) {
        val (steps, calories) = databaseHelper.getStepsAndCaloriesForDate(date)
        updateStepCount(steps)
        updateCaloriesDisplay(calories)

        val route = getRouteForDate(date)
        if (route.isNotEmpty()) {
            showRouteOnMap(route)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.first(), 15.0f))
            updateDistanceDisplay(route)
        } else {
            currentPolyline.points = emptyList()
            distanceText.text = "Distance: 0.0 km"
        }
    }

    private fun updateDistanceDisplay(route: List<LatLng>) {
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            totalDistance += calculateDistance(route[i], route[i + 1])
        }
        distanceText.text = String.format("Distance: %.2f km", totalDistance)

        val currentSteps = footStepsTextView.text.toString().toIntOrNull() ?: 0
        calculateAndUpdateCalories(currentSteps, route)
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0].toDouble() / 1000
    }

    private fun renewDataAtMidnight() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val delay = calendar.timeInMillis - System.currentTimeMillis()

        Handler(Looper.getMainLooper()).postDelayed({
            databaseHelper.clearPreviousMonthData()
            previousTotalSteps = 0f
            currentPolyline.points.clear()
            footStepsTextView.text = "0"
            caloriesTextView.text = "0 kcal"
            distanceText.text = "Distance: 0.0 km"
            val newDates = DateUtils().generateDates(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))
            adapter.updateDates(newDates)
            renewDataAtMidnight()
        }, delay)
    }

    private fun saveRouteCoordinate(latLng: LatLng) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        databaseHelper.saveRouteCoordinate(currentDate, latLng.latitude, latLng.longitude)
    }

    private fun getRouteForDate(date: String): List<LatLng> {
        return databaseHelper.getRouteForDate(date)
    }

    override fun onDateClick(date: String) {
        isViewingHistory = true
        if (::mMap.isInitialized) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = false
        }

        val formattedDate = formatToDatabaseDate(date)
        loadDataForDate(formattedDate)
    }

    private fun formatToDatabaseDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-d", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(parsedDate)
        } catch (e: Exception) {
            e.printStackTrace()
            date
        }
    }

    private fun showRouteOnMap(route: List<LatLng>) {
        currentPolyline.points = route
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))

        polylineOptions = PolylineOptions().width(10f).color(Color.YELLOW)
        currentPolyline = mMap.addPolyline(polylineOptions)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = !isViewingHistory
            setupLocationUpdates()
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        loadDataForDate(currentDate)
    }

    private fun setupLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }




    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = !isViewingHistory
                    setupLocationUpdates()
                }
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER && !isViewingHistory && serviceManager.isStepsServiceEnabled()) {
            if (previousTotalSteps == 0f) {
                previousTotalSteps = event.values[0]
            }
            totalSteps = event.values[0]
            val currentSteps = (totalSteps - previousTotalSteps).toInt()
            updateStepCount(currentSteps)

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            databaseHelper.saveStepsCount(currentDate, currentSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle changes in sensor accuracy if needed
    }

    private fun updateStepCount(steps: Int) {
        footStepsTextView.text = steps.toString()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (stepCounter != null && serviceManager.isStepsServiceEnabled()) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)
        }
        if (::mMap.isInitialized && serviceManager.isLocationServiceEnabled() &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = !isViewingHistory
            setupLocationUpdates()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Activity().apply {
                arguments = Bundle().apply {
                    // Additional parameters if needed
                }
            }
    }
}
