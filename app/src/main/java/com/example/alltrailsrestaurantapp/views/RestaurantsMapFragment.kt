package com.example.restaurantapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.alltrailsrestaurantapp.R
import com.example.alltrailsrestaurantapp.databinding.FragmentFirstBinding
import com.example.alltrailsrestaurantapp.models.Result
import com.example.alltrailsrestaurantapp.views.viewmodels.RestaurantViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import androidx.activity.result.ActivityResultCallback

import androidx.activity.result.contract.ActivityResultContracts

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission


@AndroidEntryPoint
class RestaurantsMapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var mMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    private val restaurantViewModel by activityViewModels<RestaurantViewModel>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(requireActivity())

        fetchLocation()

        lifecycleScope.launch {
            restaurantViewModel.uiState.collect {
                addMarkers(it)
            }
        }

        lifecycleScope.launch {
            restaurantViewModel.clearFields.observeForever{
                _binding?.mapSearchView?.setText("")
            }
        }

        lifecycleScope.launch {
            restaurantViewModel.errorMessage.observeForever {
                Toast.makeText(activity,it.buildMessage(context), Toast.LENGTH_SHORT).show()
            }
        }

        _binding?.mapSearchView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(searchTxt: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch {
                    restaurantViewModel.searchChannel.send(searchTxt.toString())
                }
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnList.setOnClickListener {
            val action = RestaurantsMapFragmentDirections.actionFirstFragmentToSecondFragment()
            action.latitude = currentLocation.latitude.toString()
            action.longitude = currentLocation.longitude.toString()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addMarkers(locations: List<Result>){
        try {
            mMap.clear()
            // This loop will go through all the results and add marker on each location.
            for (i in locations.indices) {
                val location: Result = locations.get(i)
                val lat: Double = location.geometry?.location?.lat ?: 0.0
                val lng: Double =
                    location.geometry?.location?.lng ?: 0.0
                val placeName: String = location?.name ?: ""
                val vicinity: String = location?.vicinity ?: ""
                val markerOptions = MarkerOptions()
                val latLng = LatLng(lat, lng)
                // Position of Marker on Map
                markerOptions.position(latLng);
                // Adding Title to the Marker
                markerOptions.title("$placeName : $vicinity")
                // Adding Marker to the Camera.
                // Adding colour to the marker
                if(location.isFavorite)
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                else
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                // move map camera
               // markerOptions.alpha(0.7f)
                val m = mMap.addMarker(markerOptions)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14f))
            }
        } catch (e: Exception) {
            // Log.d("onResponse", "There is an error")
            e.printStackTrace()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID)
    }

    private val mPermissionResult = registerForActivityResult(
        RequestPermission()
    ) { result ->
        if (result) {
            fetchLocation()
        }
    }

    fun fetchLocation(){
        if (ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED ) {
            mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                lifecycleScope.launch {
                    restaurantViewModel.currentLocationChannel.send(currentLocation)
                }
                Toast.makeText(
                    requireActivity().applicationContext, currentLocation.latitude.toString() + "" +
                            currentLocation.longitude, Toast.LENGTH_SHORT
                ).show()
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
                mapFragment?.let {
                    it.getMapAsync(this)
                }
            }
        }
    }
}