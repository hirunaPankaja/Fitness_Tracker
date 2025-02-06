package com.example.gym_workout

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_workout.R
import com.example.gym_workout.utils.DateAdapter
import com.example.gym_workout.utils.DateUtils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Additional initialization code if needed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activity, container, false)

        val dateRecyclerView: RecyclerView = view.findViewById(R.id.dateRecyclerView)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        dateRecyclerView.layoutManager = linearLayoutManager

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val dates = DateUtils().generateDates(month, year)
        val adapter = DateAdapter(dates, this)
        dateRecyclerView.adapter = adapter

        // Scroll to and center today's date
        val sdf = SimpleDateFormat("dd\nMMM", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val todayPosition = dates.indexOf(currentDate)
        if (todayPosition != -1) {
            dateRecyclerView.post {
                val offset = (dateRecyclerView.width / 2) - (view.findViewById<View>(R.id.dateRecyclerView).width / 2)
                linearLayoutManager.scrollToPositionWithOffset(todayPosition, offset)
            }
        }

        // Map initialization
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Map Panel click event
        mapPanel = view.findViewById(R.id.mapPanel)
        mapPanel.setOnClickListener {
            val intent = Intent(context, Map1::class.java)
            startActivity(intent)
        }

        // Initialize step counter
        footStepsTextView = view.findViewById(R.id.footSteps)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Register the sensor listener
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))

        val sriLanka = LatLng(7.8731, 80.7718)
        mMap.addMarker(MarkerOptions().position(sriLanka).title("Marker in Sri Lanka"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 10.0f))

        // Additional logic to track the user's walking route
        setupLocationUpdates()
    }

    private fun setupLocationUpdates() {
        // Initialize PolylineOptions and Polyline
        val polylineOptions = PolylineOptions().width(5f).color(android.graphics.Color.RED)
        val currentPolyline = mMap.addPolyline(polylineOptions)

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Update interval in milliseconds
            fastestInterval = 2000 // Fastest update interval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    polylineOptions.add(currentLatLng)
                    currentPolyline.points = polylineOptions.points
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f))
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        mMap.isMyLocationEnabled = true
    }

    override fun onDateClick(date: String) {
        val dateInformation = retrieveDateInformation(date)
    }

    private fun retrieveDateInformation(date: String): DateUtils {
        return DateUtils()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = event.values[0]
            val currentSteps = (totalSteps - previousTotalSteps).toInt()
            if (currentSteps >= 0) {
                updateStepCount(currentSteps)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateStepCount(steps: Int) {
        footStepsTextView.text = steps.toString()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Activity.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Activity().apply {
                arguments = Bundle().apply {
                    // Additional parameters if needed
                }
            }
    }
}
