package com.example.locationer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //Variables Declaration for the FusedLocationProviderClient...
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
                val place=LatLng(location.latitude,location.longitude)
                val temp=Geocoder(this, Locale.ENGLISH)
                val name=temp.getFromLocation(location.latitude,location.longitude,10)
                mMap.addMarker(MarkerOptions().position(place).title("Marker in nowhere"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place))
                Toast.makeText(
                    this, "${name[0].getAddressLine(0)}", Toast.LENGTH_LONG
                ).show()
            } else {
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
}