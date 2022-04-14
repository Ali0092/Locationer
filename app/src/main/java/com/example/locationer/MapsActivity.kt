package com.example.locationer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.locationer.Constants.REQUEST_CODE

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationer.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //SaveInstances testing ...is pending...
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //Variables Declaration for the FusedLocationProviderClient and LocationCallback...
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //variables Initialization...
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        binding.flbtn.setOnClickListener {
            getLocation()

        }
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult?:return
                for (L in locationResult.locations) {
                    val name = LatLng(L.latitude, L.longitude)
                    mMap.addMarker(MarkerOptions().position(name).title("Marker in Sydney"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(name))
                }
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    //to stop getting updates of Location On Pause...
    override fun onPause() {
        super.onPause()
        fusedLocationProvider.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(30.4, 30.2)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        PermissionGranted()
        fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val place = LatLng(location.latitude, location.longitude)
                val temp = Geocoder(this, Locale.ENGLISH)
                val name = temp.getFromLocation(location.latitude, location.longitude, 10)
                mMap.addMarker(MarkerOptions().position(place).title("Marker in nowhere"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place))

                //the shareIntentService should be called here....

                Toast.makeText(
                    this, name[0].getAddressLine(0), Toast.LENGTH_LONG
                ).show()
            } else {
                startLocationUpdates()
                Toast.makeText(
                    this, "location Unavailable", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun PermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_CODE
            )
            true

        } else false
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


}