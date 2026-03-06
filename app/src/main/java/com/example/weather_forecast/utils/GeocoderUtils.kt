package com.example.weather_forecast.utils


import android.content.Context
import android.location.Geocoder
import java.util.Locale

fun getTopBarLocation(context: Context, lat: Double, lon: Double): String {
    return try {
        val geocoder  = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val address   = addresses[0]
            val adminArea = address.adminArea
            val country   = address.countryCode
            "$adminArea, $country"
        } else "Unknown"
    } catch (e: Exception) { "Unknown" }
}

fun getCenterLocation(context: Context, lat: Double, lon: Double): String {
    return try {
        val geocoder  = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val address  = addresses[0]
            val locality = address.locality
            val subAdmin = address.subAdminArea
            when {
                locality != null && subAdmin != null -> "$locality, $subAdmin"
                locality != null -> locality
                subAdmin != null -> subAdmin
                else -> "Unknown"
            }
        } else "Unknown"
    } catch (e: Exception) { "Unknown" }
}