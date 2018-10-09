package fi.metropolia.harrytoan.gofitness.Retrofit

import android.graphics.Point
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object GoogleMapDirectionAPI {
    // URL

    val url = "https://maps.googleapis.com/maps/api/directions/"

    // Define model

    object GoogleMapModel {
        data class DirectionInfo(var routes: ArrayList<Route>)
        data class Route(val overview_polyline: Point)
        data class Point(val points: String)
    }

    // Define service

    interface DirectionService {
        @GET("json?key=AIzaSyAwqvzC07jYxKdI9jDryRw1yqhZUrcPgHc&mode=walking")
        fun getDirection(@Query("origin") location: String, @Query("destination") destination: String): Call<GoogleMapModel.DirectionInfo>
    }

    // Create and configure retrofit

    private val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service = retrofit.create(GoogleMapDirectionAPI.DirectionService::class.java)
}