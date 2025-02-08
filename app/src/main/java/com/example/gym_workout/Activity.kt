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
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_workout.Map1
import com.example.gym_workout.database.DatabaseHelper2
import com.example.gym_workout.database.DatabaseHelper2.Companion.COLUMN_DATE
import com.example.gym_workout.database.DatabaseHelper2.Companion.COLUMN_LATITUDE
import com.example.gym_workout.database.DatabaseHelper2.Companion.COLUMN_LONGITUDE
import com.example.gym_workout.database.DatabaseHelper2.Companion.TABLE_ROUTE
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

    private lateinit var polylineOptions: PolylineOptions
    private lateinit var currentPolyline: Polyline

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var databaseHelper: DatabaseHelper2

    private lateinit var adapter: DateAdapter

    private var viewingPastData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper2(requireContext())
        // Start the foreground service
        val serviceIntent = Intent(context, LocationAndStepService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
        val db = databaseHelper.writableDatabase
        arguments?.let {
            // Additional initialization code if needed
        }

        // Renew data at midnight
        renewDataAtMidnight()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_activity, container, false)

        val dateRecyclerView: RecyclerView = view.findViewById(R.id.dateRecyclerView)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        dateRecyclerView.layoutManager = linearLayoutManager

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val dates = DateUtils().generateDates(month, year)
        adapter = DateAdapter(dates, this)
        dateRecyclerView.adapter = adapter

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapPanel = view.findViewById(R.id.mapPanel)
        mapPanel.setOnClickListener {
            val intent = Intent(context, Map1::class.java)
            startActivity(intent)
        }

        footStepsTextView = view.findViewById(R.id.footSteps)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return view
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
        // Format the date to ensure it matches the database format
        val formattedDate = formatToDatabaseDate(date)
        val stepsCount = databaseHelper.getStepsCountForDate(formattedDate)
        updateStepCount(stepsCount)

        val route = getRouteForDate(formattedDate)
        if (route.isEmpty()) {
            Toast.makeText(context, "No route data available for this date", Toast.LENGTH_SHORT).show()
            currentPolyline.points.clear()
        } else {
            showRouteOnMap(route)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.first(), 15.0f))
        }

        // Disable real-time updates when viewing past data
        viewingPastData = true
    }

    // Helper function to format the date to "yyyy-MM-dd"
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
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            setupLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions are already granted, start the service
            startForegroundService()
        }
    }

    private fun startForegroundService() {
        val serviceIntent = Intent(context, LocationAndStepService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
    }

    private fun setupLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!viewingPastData) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        val points = currentPolyline.points
                        points.add(currentLatLng)
                        currentPolyline.points = points

                        saveRouteCoordinate(currentLatLng)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f))

                        Log.d("LocationUpdate", "Location: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                    setupLocationUpdates()
                }
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_STEP_COUNTER) return

        if (previousTotalSteps == 0f) {
            previousTotalSteps = event.values[0]
        }
        totalSteps = event.values[0]
        val currentSteps = (totalSteps - previousTotalSteps).toInt()
        updateStepCount(currentSteps)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        databaseHelper.saveStepsCount(currentDate, currentSteps)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

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
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)
        if (::mMap.isInitialized && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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
