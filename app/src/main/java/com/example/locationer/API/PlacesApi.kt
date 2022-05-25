package com.example.locationer.API

import com.example.locationer.Constants.API_KEY
import com.example.locationer.Constants.GET_REQUEST
import com.example.locationer.Constants.RADIUS
import com.example.locationer.Constants.TYPE
import com.example.locationer.model.nearbysearch.NearbySearch
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {

    @GET(GET_REQUEST)
    fun getNearbyPlaces(
        @Query("location") location:String,
        @Query("radius") radius:Int ,
        @Query("type") type:String ,
        @Query("key") key:String
    ): Call<NearbySearch>

}