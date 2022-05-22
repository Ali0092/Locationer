package com.example.locationer

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class DataParse {

    //to get single place...
    private fun getPlace(googlePlaceJson: JSONObject): HashMap<String, String> {
        var googlePlaceMap: HashMap<String, String> = HashMap()
        var placeName: String = "-/NA"
        var vicinity: String = "-/NA"
        var lat: String = ""
        var long: String = ""
        var reference: String = ""

        try {
            if (googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name")
            }
            if (googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity")
            }

            lat = googlePlaceJson.getJSONObject("geometry").getJSONObject("location")
                .getString("latitude")
            long = googlePlaceJson.getJSONObject("geometry").getJSONObject("location")
                .getString("longitude")

            reference = googlePlaceJson.getString("reference")

            googlePlaceMap.put("place_name", placeName)
            googlePlaceMap.put("vicinity", vicinity)
            googlePlaceMap.put("latitude", lat)
            googlePlaceMap.put("longitude", long)
            googlePlaceMap.put("reference", reference)


        } catch (e: IOException) {
            e.printStackTrace()
        }

        return googlePlaceMap
    }

    //to get list of places....
    private fun getAllNearbyPlaces(jsonArray: JSONArray): List<HashMap<String, String>> {
        val counter = jsonArray.length()
        val nearbyPlaces = mutableListOf<HashMap<String, String>>()
        var nearbyPlaceMap: HashMap<String, String>

        for (i in 0..counter) {
            try {
                nearbyPlaceMap = getPlace(jsonArray.get(i) as JSONObject)
                nearbyPlaces.add(nearbyPlaceMap)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return nearbyPlaces

    }

     fun parse(jsonData:String):List<HashMap<String, String>>{
        var jsonArray:JSONArray= JSONArray()
        var jsonObject:JSONObject=JSONObject()

        try{

            jsonObject= JSONObject(jsonData)
            jsonArray=jsonObject.getJSONArray("results")

        }catch (e:Exception){
            e.printStackTrace()
        }
        return getAllNearbyPlaces(jsonArray)
    }
}