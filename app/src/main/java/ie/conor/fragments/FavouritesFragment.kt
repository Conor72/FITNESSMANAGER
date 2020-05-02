package ie.conor.fragments

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.conor.R
import ie.conor.main.FitnessApp
import ie.conor.utils.getAllFitnessx
import ie.conor.utils.getFavouriteFitnessx
import ie.conor.utils.setMapMarker
import ie.conor.utils.trackLocation
import kotlinx.android.synthetic.main.fragment_favourites.*


class FavouritesFragment : Fragment() {

    lateinit var app: FitnessApp
    var viewFavourites = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as FitnessApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_favourites, container, false)

        return layout;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.favourites_title)

        imageMapFavourites.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                app.mMap.clear()
                setMapMarker(app)
                if (!viewFavourites) {
                    imageMapFavourites.setImageResource(R.drawable.ic_favorite_on)
                    viewFavourites = true
                    getFavouriteFitnessx(app)
                }
                else {
                    imageMapFavourites.setImageResource(R.drawable.ic_favorite_off)
                    viewFavourites = false
                    getAllFitnessx(app)
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavouritesFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}