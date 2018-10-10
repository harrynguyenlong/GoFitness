package fi.metropolia.harrytoan.gofitness

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment(), SensorEventListener {

    private var isRunning = false

    private var sensorManager: SensorManager? = null

    private var mtraveledDistance: Float = 0.toFloat()
        set(value) {
            field = value
            didSetTraveledDistance()
        }

    private var mCurrentLocation: LatLng? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationRequest = LocationRequest().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.lastLocation?.let {

                val newLocation = LatLng(it.latitude, it.longitude)

                val userNewLocation = Location("")
                userNewLocation.latitude = newLocation.latitude
                userNewLocation.longitude = newLocation.longitude

                mCurrentLocation?.let {

                    val currentLocation = Location("")
                    currentLocation.latitude = it.latitude
                    currentLocation.longitude = it.longitude

                    mtraveledDistance += userNewLocation.distanceTo(currentLocation)

                    mCurrentLocation = newLocation

                }

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val context = context as? MainActivity

        sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onPause() {
        super.onPause()

        isRunning = false
        sensorManager?.unregisterListener(this)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        isRunning = true

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            mCurrentLocation = LatLng(location!!.latitude, location!!.longitude)
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        )

        var stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(context!!, "No sensor found!", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        mtraveledDistance = sharedPref.getFloat(getString(R.string.traveledDistance), 0.toFloat())


    }

    companion object {
        fun newInstance(): UserFragment = UserFragment()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(isRunning) {
            step.text = "${event!!.values[0]}"
        }
    }

    private fun didSetTraveledDistance() {

        if (traveledDistanceTxt != null) {
            traveledDistanceTxt.text = "%.2f".format(mtraveledDistance)

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

            with(sharedPref.edit()) {
                putFloat(getString(R.string.traveledDistance), mtraveledDistance)
                commit()
            }

            val showFirstNotif = sharedPref.getBoolean(getString(R.string.isNotificationShown), false)

            if (!showFirstNotif && mtraveledDistance > 10) {
                val intent = Intent("complete-first-100m")
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)

                with(sharedPref.edit()) {
                    putBoolean(getString(R.string.isNotificationShown), true)
                    commit()
                }
            }
        }
    }
}