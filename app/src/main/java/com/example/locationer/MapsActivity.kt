package com.example.locationer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.locationer.Constants.REQUEST_CODE

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationer.databinding.ActivityMapsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.Marker
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback ,
GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    //SaveInstances testing ...is pending...
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var googleApiClient:GoogleApiClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var lastLocation:Location
    private lateinit var currentLocationMarker:Marker


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            checkLocationPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            buildGoogleApiClient()
            mMap.isMyLocationEnabled=true
        }


    }
    private fun buildGoogleApiClient(){
        googleApiClient=GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener (this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient.connect()

    }
    override fun onConnected(p0: Bundle?) {
        locationRequest= LocationRequest()
        locationRequest.setInterval(1100)
        locationRequest.setFastestInterval(1100)
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location) {
        lastLocation=location

       // currentLocationMarker.remove()
        val latLng=LatLng(location.latitude,location.longitude)
        val markerOptions=MarkerOptions().position(latLng)
        markerOptions.title("Current Location...")
        currentLocationMarker= mMap.addMarker(markerOptions)!!
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
  //      mMap.animateCamera(CameraUpdateFactory.zoomBy(14f))

        if(googleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this)
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
           REQUEST_CODE -> {
               if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   if (ActivityCompat.checkSelfPermission(
                           this,
                           Manifest.permission.ACCESS_FINE_LOCATION
                       ) ==PackageManager.PERMISSION_GRANTED  ){
                       if (googleApiClient==null){
                           buildGoogleApiClient()
                       }
                       mMap.isMyLocationEnabled=true
                   }
               }
               else{
                   Toast.makeText(this,"Permission Denied...",Toast.LENGTH_LONG).show()
               }
               return
           }
       }
    }

    private fun checkLocationPermission(): Boolean {
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


/*
  PermissionGranted()
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(name,15f))
                    mMap.addMarker(MarkerOptions().position(name).title("Marker in Sydney"))
                                 }
            }
        }

 @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val place = LatLng(location.latitude, location.longitude)
                val temp = Geocoder(this, Locale.ENGLISH)
                val name = temp.getFromLocation(location.latitude, location.longitude, 10)
                mMap.addMarker(MarkerOptions().position(place).title("Marker in nowhere"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place,15f))

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

* */