package com.example.locationer

import android.os.AsyncTask
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.net.URL

class GetNearbyLocation:AsyncTask<Any, String, String>() {

    private var googlePlaceData:String =" "
    private var url:String=""
    private lateinit var gMap:GoogleMap

    override fun doInBackground(vararg objects: Any?): String {
        gMap= objects[0] as GoogleMap
        url=objects[1] as String

        try {

        }catch (e: IOException){
            googlePlaceData=DownloadURL().readUrl(url)
        }
        return googlePlaceData
    }


    override fun onPostExecute(result: String?) {
        val nearbyPlacesList:List<HashMap<String, String>>
        val dataParse=DataParse()
        nearbyPlacesList=dataParse.parse(result!!)
        displayNearbyPlaces(nearbyPlacesList)
    }

     fun displayNearbyPlaces(nearbyPlacesList:List<HashMap<String, String>>){
        for ( i in nearbyPlacesList.indices ){
            var markerOptions:MarkerOptions= MarkerOptions()

            val googleNearbyPlace:HashMap<String,String> =nearbyPlacesList.get(i)
            val placeName=googleNearbyPlace.get("place_name")
            val vicinity=googleNearbyPlace.get("vicinity")
            val lat= googleNearbyPlace.get("latitude")?.toDouble()
            val lon= googleNearbyPlace.get("longitude")?.toDouble()

            val latLng=LatLng(lat!!, lon!!)

            markerOptions=MarkerOptions().position(latLng)
            markerOptions.title(placeName + " : " + vicinity)
            gMap.addMarker(markerOptions)!!
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            gMap.animateCamera(CameraUpdateFactory.zoomBy(14f))
        }
    }




}