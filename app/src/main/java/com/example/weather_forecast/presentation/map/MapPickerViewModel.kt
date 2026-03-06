package com.example.weather_forecast.presentation.map

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.models.FavoriteLocationStat
import com.example.weather_forecast.presentation.favourite.FavouriteViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapPickerViewModel(private val app: Application) : ViewModel() {
    private val _pickedLatLng = MutableStateFlow<LatLng?>(null)
    val pickedLatLng: StateFlow<LatLng?> get() = _pickedLatLng

    private val _pickedName = MutableStateFlow("")
    val pickedName: StateFlow<String> get() = _pickedName
init{
    initPlaces()
}

fun initPlaces(){
    if (!Places.isInitialized()) {
        Places.initialize(app, "AIzaSyCJLsLgqW_MrzD7861dn16hNxVpfxCqxfU")
    }
}

    fun onPlacePicked(latLng: LatLng, name: String) {
        _pickedLatLng.value = latLng
        _pickedName.value   = name
    }

    fun onMapTapped(latLng: LatLng) {
        _pickedLatLng.value = latLng
        _pickedName.value   = "Pinned Location"


        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder  = Geocoder(app, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val address   = addresses?.firstOrNull()

                val name = when {
                    address?.locality    != null -> address.locality
                    address?.subAdminArea != null -> address.subAdminArea
                    address?.adminArea   != null -> address.adminArea
                    else                         -> "Pinned Location"
                }

                withContext(Dispatchers.Main) {
                    if (_pickedLatLng.value == latLng) {
                        _pickedName.value = name
                    }
                }
            } catch (e: Exception) {

            }}}

    fun clearPin() {
        _pickedLatLng.value = null
        _pickedName.value   = ""
    }
    fun onLocationPicked(
        lat: Double,
        lng: Double,
        name: String,
        favouriteViewModel: FavouriteViewModel
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder  = Geocoder(app, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val address   = addresses?.firstOrNull()

                val cityName    = address?.locality    ?: name
                val countryName = address?.countryName ?: ""
                val countryCode = address?.countryCode ?: ""

                val item = FavoriteLocationStat(
                    cityName = cityName,
                    countryName = countryName,
                    countryCode = countryCode,
                    temp = 0,
                    highTemp = 0,
                    lowTemp = 0,
                    weatherCondition = "",
                    humidity = 0,
                    windSpeed = 0.0,
                    iconUrl = null
                )

                withContext(Dispatchers.Main) {
                    favouriteViewModel.addFavourite(item)
                    clearPin()
                }
            } catch (e: Exception) {
                clearPin()
            }
        }
    }
}



class MapPickerViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapPickerViewModel(app) as T
    }
}