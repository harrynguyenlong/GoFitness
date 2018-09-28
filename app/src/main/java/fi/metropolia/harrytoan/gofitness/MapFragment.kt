package fi.metropolia.harrytoan.gofitness

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMapView: MapView

    private lateinit var mGoogleMap: GoogleMap

    private lateinit var mView: View

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        mView =  inflater.inflate(R.layout.fragment_map, container, false)

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

    override fun onResume() {

        mMapView.onResume()

        super.onResume()

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(context)

        mGoogleMap = googleMap!!

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

    }

}
