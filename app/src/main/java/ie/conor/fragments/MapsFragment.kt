package ie.conor.fragments

import android.os.Bundle
import com.google.android.gms.maps.*
import ie.conor.main.FitnessApp
import ie.conor.utils.*


class MapsFragment : SupportMapFragment(), OnMapReadyCallback{

    lateinit var app: FitnessApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as FitnessApp
        getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        app.mMap = googleMap
        app.mMap.isMyLocationEnabled = true
        app.mMap.uiSettings.isZoomControlsEnabled = true
        app.mMap.uiSettings.setAllGesturesEnabled(true)
        app.mMap.clear()
        trackLocation(app)
        setMapMarker(app)
        getAllFitnessx(app)
    }
}