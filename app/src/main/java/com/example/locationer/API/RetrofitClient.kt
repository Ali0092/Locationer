package com.example.locationer.API

import com.example.locationer.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun getPlacesAPI(): PlacesApi {
        val retrofit=Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient().newBuilder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(PlacesApi::class.java)
    }
}