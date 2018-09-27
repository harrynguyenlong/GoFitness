package fi.metropolia.harrytoan.gofitness

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.metropolia.harrytoan.gofitness.Retrofit.WeatherAPI
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface HomeFragmentInterface {
    fun onLetsGoButtonClicked()
}

class HomeFragment : Fragment() {

    private var listener: HomeFragmentInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goToMapBtn.setOnClickListener {
            listener?.onLetsGoButtonClicked()
        }

    }

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
}