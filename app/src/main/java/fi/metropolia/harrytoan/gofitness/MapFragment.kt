package fi.metropolia.harrytoan.gofitness

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*
import android.support.v4.content.ContextCompat
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.graphics.drawable.DrawableCompat
import android.os.Build
import com.google.android.gms.location.*
import fi.metropolia.harrytoan.gofitness.Room.CandyRoomModel
import fi.metropolia.harrytoan.gofitness.Room.ViewModel


@SuppressLint("ValidFragment")
class MapFragment(private val viewModel: ViewModel) : Fragment(), OnMapReadyCallback {


    private lateinit var mMapView: MapView

    private lateinit var mGoogleMap: GoogleMap

    private lateinit var mView: View

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationRequest = LocationRequest().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.lastLocation?.let {

                val currentLocation = LatLng(it.latitude, it.longitude)

                mCurrentLocation = currentLocation

                val userLocation = Location("")
                userLocation.latitude = currentLocation.latitude
                userLocation.longitude = currentLocation.longitude

                for (candy in listOfCandies) {
                    val candyLocation = Location("")
                    candyLocation.latitude = candy.location!!.latitude
                    candyLocation.longitude = candy.location!!.longitude


                    candy.IsCatch = userLocation.distanceTo(candyLocation) < 100
                }
            }
        }
    }

    private var listOfCandies = ArrayList<Candy>()

    private var mCurrentLocation: LatLng? = null
        set(value: LatLng?) {
            field = value
              didSetCurrentLocation()
        }

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_map, container, false)

        loadStaticListOfCandies()

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = map

        if (mMapView != null) {
            mMapView.onCreate(null)
            mMapView.onResume()
            mMapView.getMapAsync(this)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()

        mMapView.onDestroy()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {

        mMapView.onResume()

        super.onResume()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            mCurrentLocation = LatLng(location!!.latitude, location!!.longitude)
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        )

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(context)

        mGoogleMap = googleMap!!

        mGoogleMap.isMyLocationEnabled = true

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        for (candy in listOfCandies) {
            val candyLocation = LatLng(candy.location!!.latitude, candy.location!!.longitude)

            googleMap.addMarker(MarkerOptions()
                    .position(candyLocation)
                    .title(candy.name)
                    .snippet(candy.des + ", power: ${candy.amount}")
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context!!, R.drawable.ic_candy))))
        }

    }

    private fun didSetCurrentLocation() {

        if (mCurrentLocation != null) {
            // Do sth with current Location

            val center = CameraPosition.builder()
                    .target(LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude))
                    .zoom(16.toFloat())
                    .bearing(0.toFloat())
                    .tilt(15.toFloat())
                    .build()

            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(center))
        }

    }

    private fun loadStaticListOfCandies() {

        val candiesInRoomDB = viewModel.allCandies

        candiesInRoomDB.value?.let { candies ->
            if (candies.count() == 0) {
                // Nothing in the DB
                println("Nothing")
            } else {
                // There are some candies saved from the DB

                println("SomeThing")
            }
        }

    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }

        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)

        return bitmap
    }

    fun updateMap(candies: List<CandyRoomModel>) {

        // Setting list of candies goes here



        // Then update mMapView

        mMapView.invalidate()
    }

}
