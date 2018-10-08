package fi.metropolia.harrytoan.gofitness

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import fi.metropolia.harrytoan.gofitness.R.id.*
import fi.metropolia.harrytoan.gofitness.Retrofit.WeatherAPI
import fi.metropolia.harrytoan.gofitness.Room.CandyRoomModel
import fi.metropolia.harrytoan.gofitness.Room.ViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

interface HomeFragmentInterface {
    fun onLetsGoButtonClicked()
}

class HomeFragment : Fragment() {

    var listener: HomeFragmentInterface? = null

    lateinit var candyListViewModel: ViewModel

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val context = activity as? Context

        candyListViewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)

        candyListViewModel.allCandies.observe(this, android.arch.lifecycle.Observer {
            it?.let {
                updateUI(it)
            }
        })

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goToMapBtn.setOnClickListener {
            listener?.onLetsGoButtonClicked()
        }

    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        val weatherService = WeatherAPI.service

        val value = object : Callback<WeatherAPI.Model.WeatherInfo> {
            override fun onFailure(call: Call<WeatherAPI.Model.WeatherInfo>, t: Throwable) {
                Log.d("weather", t.localizedMessage)
            }

            override fun onResponse(call: Call<WeatherAPI.Model.WeatherInfo>, response: Response<WeatherAPI.Model.WeatherInfo>) {
                if (response != null) {
                    Log.d("Value", "${response.body()!!.main.temp}")

                    assignDegree(response.body()!!.main.temp)
                }
            }
        }

        weatherService.getInfoFromWeatherAPI("helsinki").enqueue(value)

        getCurrentTime()

        geoCoder = Geocoder(context, Locale.getDefault())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            currentLocation = location
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is HomeFragmentInterface) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }


    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    private fun assignDegree(kevinDegree: Double) {
        val celciusDegree = kevinDegree - 273.15

        degree.text = "${celciusDegree.toInt()}°C"
    }

    private fun didSetCurrentLocation() {

        if (currentLocation != null) {
            homeTown = parseLocationToAddress(currentLocation!!)

        }

    }

    private fun didSetHomeTown() {
        cityName.text = homeTown
    }

    private fun parseLocationToAddress(location: Location): String {

        val address = geoCoder?.getFromLocation(location.latitude, location.longitude, 1)

        if (address?.get(0)?.locality != null) {
            return address?.get(0)?.locality!!
        } else {
            return ""
        }

    }

    private fun getCurrentTime() {

        val currentDate = Date(Calendar.getInstance().timeInMillis)

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

        var dateString = dateFormatter.format(currentDate)

        timeStamp.text = dateString
    }

    private fun updateUI(candies: List<CandyRoomModel>) {
        var numberOfCandies = 0

        for (candy in candies) {
            if (candy.isCatch) {
                numberOfCandies += candy.amount.toInt()
            }
        }

        numberOfCandiesTxt.text = numberOfCandies.toString()

    }
}