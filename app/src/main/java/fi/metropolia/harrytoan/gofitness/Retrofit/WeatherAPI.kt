package fi.metropolia.harrytoan.gofitness.Retrofit


import android.view.Display
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WeatherAPI {
    // URL

    val url = "http://api.openweathermap.org/data/2.5/"

    // Define model

    object Model {
        data class WeatherInfo(var main: MainInfo)
        data class MainInfo(var temp: Double)
    }

    // Define service

    interface WeatherService {
        @GET("weather?appid=ac4100951c12a5f6302baf66ac3292e3")
        fun getInfoFromWeatherAPI(@Query("q") location: String): Call<Model.WeatherInfo>
    }

    // Create and configure retrofit

    private val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service = retrofit.create(WeatherService::class.java)
}