package fi.metropolia.harrytoan.gofitness

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.fragment_map.*
import android.support.v4.content.ContextCompat
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.graphics.drawable.DrawableCompat
import android.os.Build
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import fi.metropolia.harrytoan.gofitness.Retrofit.GoogleMapDirectionAPI
import fi.metropolia.harrytoan.gofitness.Room.CandyRoomModel
import fi.metropolia.harrytoan.gofitness.Room.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface MapFragmentDelegate {
    fun didCatchCandy()
}

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    var listener: MapFragmentDelegate? = null

    lateinit var candyListViewModel: ViewModel

    private val polylines = ArrayList<Polyline>()

    private var isMapCenteredFirstTime = false

    private val directionService = GoogleMapDirectionAPI.service

    private val value = object : Callback<GoogleMapDirectionAPI.GoogleMapModel.DirectionInfo> {
        override fun onFailure(call: Call<GoogleMapDirectionAPI.GoogleMapModel.DirectionInfo>, t: Throwable) {
            Log.d("weather", t.localizedMessage)
        }

        override fun onResponse(call: Call<GoogleMapDirectionAPI.GoogleMapModel.DirectionInfo>, response: Response<GoogleMapDirectionAPI.GoogleMapModel.DirectionInfo>) {
            if (response != null) {
                Log.d("Value", "${response.body()!!.routes.first().overview_polyline.points}")

                val option = PolylineOptions().apply {
                    color(Color.RED)
                    width(5f)
                }

                val LatLongB = LatLngBounds.Builder()

                val listOfPoints = decodePoly(response.body()!!.routes.first().overview_polyline.points)

                option.add(mCurrentLocation)

                LatLongB.include(mCurrentLocation)

                for (point in listOfPoints) {
                    option.add(point)
                    LatLongB.include(point)
                }

                val bounds = LatLongB.build()

                polylines.add(mGoogleMap!!.addPolyline(option))

                mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

            }
        }
    }

    private lateinit var mMapView: MapView

    private var mGoogleMap: GoogleMap? = null

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

                listOfCandies.forEachIndexed { index, candy ->
                    val candyLocation = Location("")
                    candyLocation.latitude = candy.latitude
                    candyLocation.longitude = candy.longitude

                    if (userLocation.distanceTo(candyLocation) < 100 && !candy.isCatch) {
                        candy.isCatch = true
                        candyListViewModel.update(candy)
                        listener?.didCatchCandy()
                    } else {
                        // Do nothing
                    }
                }
            }
        }
    }

    private var listOfCandies = ArrayList<CandyRoomModel>()

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


        val context = activity as? Context

        candyListViewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)

        candyListViewModel.allCandies.observe(this, Observer {
            it?.let {
                updateMap(it)
            }
        })

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

        mGoogleMap?.setOnMarkerClickListener(this)

        mGoogleMap?.isMyLocationEnabled = true

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        didSetListOfCandies()

    }

    private fun didSetCurrentLocation() {

        if (mCurrentLocation != null && !isMapCenteredFirstTime) {

            // Do sth with current Location

            val center = CameraPosition.builder()
                    .target(LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude))
                    .zoom(16.toFloat())
                    .bearing(0.toFloat())
                    .tilt(15.toFloat())
                    .build()

            mGoogleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(center))

            isMapCenteredFirstTime = true
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

        if (candies.count() == 0) {

            println(candies.count())

            candyListViewModel.insertCandy(CandyRoomModel("Sello", "Shopping mall", 20.toDouble(), false, 60.218140, 24.812820))
            candyListViewModel.insertCandy(CandyRoomModel("Laurea", "School", 20.toDouble(), false, 60.220520, 24.807170))
            candyListViewModel.insertCandy(CandyRoomModel("Kilonrinne 10", "Home", 20.toDouble(), false, 60.220800, 24.777650))
            candyListViewModel.insertCandy(CandyRoomModel("Le PA", "Football Stadium", 20.toDouble(), false, 46.187080, 13.303170))
            candyListViewModel.insertCandy(CandyRoomModel("Le PA", "Football Stadium", 20.toDouble(), false, 60.222720, 24.805510))

        } else {

            println(candies.count())

            listOfCandies = ArrayList(candies)

        }

    }

    private fun didSetListOfCandies() {

        // Refresh the mapView

        if (mGoogleMap != null) {

            mGoogleMap?.clear()

            for (candy in listOfCandies) {

                if (!candy.isCatch) {

                    val candyLocation = LatLng(candy.latitude, candy.longitude)

                    mGoogleMap?.addMarker(MarkerOptions()
                            .position(candyLocation)
                            .title(candy.name)
                            .snippet(candy.des + ", power: ${candy.amount}")
                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context!!, R.drawable.ic_candy))))

                }

            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        mCurrentLocation?.let {
            directionService.getDirection("${it.latitude},${it.longitude}", "${marker!!.position.latitude},${marker!!.position.longitude}")
                    .enqueue(value)

            while (polylines.count() > 0) {
                polylines[0].remove()
                polylines.removeAt(0)
            }

            polylines.clear()

        }

        return true
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

}
