package fi.metropolia.harrytoan.gofitness

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.TextKeyListener.clear
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), MapFragment.OnFragmentInteractionListener, HomeFragmentInterface {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentLocation: Location? = null
        set(value: Location?) {
            field = value
            didSetCurrentLocation()
        }

    private var geoCoder: Geocoder? = null

    private var homeTown: String = ""
        set(value) {
            field = value
            didSetHomeTown()
        }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val homeFragment = HomeFragment.newInstance()
                openFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_user -> {
                val userFragment = UserFragment.newInstance()
                openFragment(userFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_maps -> {
                val mapFragment = MapFragment.newInstance()
                openFragment(mapFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        askForPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
        askForPermission(android.Manifest.permission.INTERNET)


        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment()).commit()

        geoCoder = Geocoder(this, Locale.getDefault())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            currentLocation = location
        }

    }

    override fun onStart() {
        super.onStart()

        getCurrentTime()
    }

    override fun onMapClicked() {
        println(1234)
    }

    override fun onLetsGoButtonClicked() {
        navigationView.selectedItemId = R.id.navigation_maps
    }

    private fun askForPermission(permission: String) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
        }
    }

    private fun didSetCurrentLocation() {

        if (currentLocation != null) {
            homeTown = parseLocationToAddress(currentLocation!!)

            getCelciusDegreeForCurrentLocation(currentLocation!!)
        }

    }

    private fun getCelciusDegreeForCurrentLocation(location: Location): String {
        return ""
    }

    private fun parseLocationToAddress(location: Location): String {

        val address = geoCoder?.getFromLocation(location.latitude, location.longitude, 1)

        if (address?.get(0)?.locality != null) {
            return address?.get(0)?.locality!!
        } else {
            return ""
        }

    }

    private fun didSetHomeTown() {
        cityName.text = homeTown
    }

    private fun getCurrentTime() {

        val currentDate = Date(Calendar.getInstance().timeInMillis)

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

        var dateString = dateFormatter.format(currentDate)

        timeStamp.text = dateString
    }
}
