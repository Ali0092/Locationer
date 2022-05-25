package com.example.locationer

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.icu.text.DateTimePatternGenerator.PatternInfo.OK
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.locationer.API.RetrofitClient
import com.example.locationer.Constants.API_KEY
import com.example.locationer.Constants.RADIUS
import com.example.locationer.Constants.REQUEST_CODE
import com.example.locationer.Constants.TYPE
import com.example.locationer.databinding.ActivityMapsBinding
import com.example.locationer.model.PlaceData
import com.example.locationer.model.nearbysearch.NearbySearch
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //SaveInstances testing ...is pending...
    private var map: GoogleMap? = null
    private lateinit var binding: ActivityMapsBinding
    private var mSpotMarkerList = ArrayList<Marker>()


    //Places actually creates and manges the client to get location..
    //PlacesClient actually retrieves the current location whereas Places class creates and manages the clients to get location...
    private lateinit var placesClient: PlacesClient

    //fusedLocationProviderClient...
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private var lastLatLng: LatLng? = null
    private var cameraPosition: CameraPosition? = null

    //Default location...
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)

    //Some Constants...
    private val DEFAULT_ZOOM = 15


    //OnCreate Activity Function...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable("location")
            lastLatLng = savedInstanceState.getParcelable("latlng")
            cameraPosition = savedInstanceState.getParcelable("camera_position")
        }

        setContentView(binding.root)

        Places.initialize(this, API_KEY)
        placesClient = Places.createClient(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googleMAP) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onSaveInstanceState(outState: Bundle) {
        if (map != null) {
            outState.putParcelable("camera_position", map!!.cameraPosition)
            outState.putParcelable("location", lastKnownLocation)
            outState.putParcelable("latlng", lastLatLng)
        }
        super.onSaveInstanceState(outState)
    }

    //OnMapReady Function call back handles the map when it is ready...
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateLocationUI()
        showCurrentPlace()
        getDeviceLocation()
    }

    //get the current location and set the set the position of the map...
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result

                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                          //  Log.d("LastLocation","${lastKnownLocation!!.latitude}, ${lastKnownLocation!!.longitude}")

                            map?.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    )
                                )
                            )
                            lastLatLng =
                                LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                            getAllNearbyPlaces()

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.")
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    defaultLocation, DEFAULT_ZOOM.toFloat()
                                )
                            )
                            map?.uiSettings?.isMyLocationButtonEnabled = false
                            map?.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        defaultLocation.latitude,
                                        defaultLocation.longitude
                                    )
                                )
                            )

                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    //Function to Get the Location usage Permission...
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    //Function callback runs to request for permission and handles the request results...
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
                lastKnownLocation = null
                lastLatLng = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }


    }

    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            val placeFields: List<Place.Field> = Arrays.asList(
                Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
            val request = FindCurrentPlaceRequest.newInstance(placeFields)
            placesClient.findCurrentPlace(request)

        } else {
            Toast.makeText(this, "permission not granted...", Toast.LENGTH_LONG).show()

            map!!.addMarker(
                MarkerOptions()
                    .title("some place")
                    .snippet("SOME WHERE IN NO WHERE...")
            )

            // Prompt the user for permission.
            getLocationPermission()
        }
    }

    private fun getAllNearbyPlaces() {

        val temp = getLotLong()
        val position=temp.latitude.toString()+","+temp.longitude.toString()
        //val position=LatLng(defaultLocation.latitude,defaultLocation.longitude)
        val placesCall =
            RetrofitClient.getPlacesAPI().getNearbyPlaces(position, RADIUS, TYPE, API_KEY)
        placesCall.enqueue(object : retrofit2.Callback<NearbySearch> {

            override fun onResponse(call: Call<NearbySearch>, response: Response<NearbySearch>) {
                val nearbySearch = response.body()!!

                if (nearbySearch.status=="OK") {
                    val placesData = ArrayList<PlaceData>()
                    try {

                        for (resultItems in nearbySearch.results) {
                            val lat = resultItems.geometry.location.lat
                            val lon = resultItems.geometry.location.lng
                            val place = PlaceData(resultItems.name, lat, lon)
                            placesData.add(place)
                        }
                       // Toast.makeText(this@MapsActivity, placesData.size.toString(), Toast.LENGTH_LONG).show()

                        setMarkerAndZoom(placesData)
                    } catch (e: NullPointerException) {
                        Toast.makeText(this@MapsActivity, e.toString(), Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this@MapsActivity, nearbySearch.status, Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<NearbySearch>, t: Throwable) {
                Toast.makeText(this@MapsActivity, "Response Failure...", Toast.LENGTH_LONG).show()
            }

        })

    }

    fun setMarkerAndZoom(places: List<PlaceData>) {

        for (place in places) {
            val name = place.name
            val lat = place.lat
            val lon = place.lon
            val position = LatLng(lat!!, lon!!)
            val markerOptions = MarkerOptions()
            markerOptions.position(position).title(name)

            val marker = map!!.addMarker(markerOptions)
            mSpotMarkerList.add(marker!!)
            Toast.makeText(this@MapsActivity, "check...", Toast.LENGTH_LONG).show()
        }
    //    map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(getLotLong(), 500f))
    }

    //to get the Latitude and Longitude...
    fun getLotLong(): LatLng {
        val temp: LatLng
        if (lastKnownLocation != null) {
            temp = lastLatLng!!
        } else {
            temp = defaultLocation
        }
        return temp
    }

    fun getDesiredLocation(){
        val client = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522%2C151.1957362&radius=1500&type=restaurant&keyword=cruise&key=$API_KEY")
            .method("GET", null)
            .build()
        val response: okhttp3.Response = client.newCall(request).execute()

        val nearbySearch=response.body()
    }
}