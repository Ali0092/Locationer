package com.example.locationer.model.nearbysearch


import com.google.gson.annotations.SerializedName

data class NearbySearch(
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any>,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("status")
    val status: String
)