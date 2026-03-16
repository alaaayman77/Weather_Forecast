package com.example.weather_forecast.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

class LocationProvider(val app: Application, val fusedClient: FusedLocationProviderClient) {

    fun isLocationEnabled(): Boolean {
        val locationManager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun getFreshLocation(onLocationReady: (Location) -> Unit) {

        fusedClient.lastLocation.addOnSuccessListener { lastLocation ->
            if (lastLocation != null) {
                onLocationReady(lastLocation)
                return@addOnSuccessListener
            }

            requestFreshLocation(onLocationReady)
        }.addOnFailureListener {
            requestFreshLocation(onLocationReady)
        }
    }

    private fun requestFreshLocation(onLocationReady: (Location) -> Unit) {
        var callback: LocationCallback? = null
        val handler = android.os.Handler(Looper.getMainLooper())

        callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                handler.removeCallbacksAndMessages(null) // cancel timeout
                fusedClient.removeLocationUpdates(this)
                locationResult.lastLocation?.let { onLocationReady(it) }
            }
        }

        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            callback,
            Looper.getMainLooper()
        )

        handler.postDelayed({
            fusedClient.removeLocationUpdates(callback!!)
            fusedClient.lastLocation.addOnSuccessListener { fallback ->
                if (fallback != null) {
                    onLocationReady(fallback)
                }

            }
        }, 10_000L)
    }
}

