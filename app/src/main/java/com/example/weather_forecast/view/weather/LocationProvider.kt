package com.example.weather_forecast.view.weather

import android.app.Application
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.LocationRequest
import android.os.Looper
import android.provider.Settings

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationProvider(val app: Application , val fusedClient : FusedLocationProviderClient) {

    fun isLocationEnabled() : Boolean{
        val locationManager : LocationManager= app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        app.startActivity(intent)
    }

    fun getFreshLocation(onLocationReady: (Location) -> Unit){
        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.lastLocation?.let {location-> onLocationReady(location) }


                }
            },
            Looper.getMainLooper()
        )
    }
}