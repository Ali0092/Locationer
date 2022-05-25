package com.example.locationer.API

import com.example.locationer.model.nearbysearch.NearbySearch
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {
    @GET("place/nearbysearch/json")
    fun getNearbyPlaces(
        @Query("location") location:LatLng,
        @Query("radius") radius:Int,
        @Query("type") type:String,
        @Query("key") key:String
    ): Call<NearbySearch>
}