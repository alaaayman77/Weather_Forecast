package com.example.weather_forecast.view.weather

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient

class WeatherViewModel(private val locationProvider: LocationProvider) : ViewModel(){
    private lateinit var fusedClient : FusedLocationProviderClient

    private val _locationState = MutableLiveData<Location>()
    val locationState : LiveData<Location>
        get() = _locationState

    fun checkLocationAndFetch(){
        if(locationProvider.isLocationEnabled()){
            locationProvider.getFreshLocation{location ->
                _locationState.value = location
            }
        }else{
            locationProvider.enableLocationServices()
        }
    }


}

class WeatherViewModelFactory(private val locationProvider: LocationProvider) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(locationProvider) as T
    }
}

