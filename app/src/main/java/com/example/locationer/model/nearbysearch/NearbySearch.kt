package com.example.locationer.model.nearbysearch

import com.example.locationer.model.PlaceData
import com.google.android.libraries.places.api.net.PlacesStatusCodes

data class NearbySearch(
    val html_attribution: Array<String>? =null ,
    val result:Array<PlaceData>? = null ,
    val statusCodes: PlacesStatusCodes
)
