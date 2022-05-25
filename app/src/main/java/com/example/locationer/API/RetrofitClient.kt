package com.example.locationer.API

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL="https://maps.googleapis.com/maps/api/"

    fun getPlacesAPI(): PlacesApi {
        val retrofit=Retrofit.Builder()
            .baseUrl(RetrofitClient.BASE_URL)
            .client(OkHttpClient().newBuilder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(PlacesApi::class.java)
    }
}