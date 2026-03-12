package com.example.weather_forecast.presentation.map

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.presentation.favourite.FavouriteViewModel
import com.example.weather_forecast.presentation.weather.UiState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapPickerViewModel(
    private val app: Application,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _pickedLatLng = MutableStateFlow<LatLng?>(null)
    val pickedLatLng: StateFlow<LatLng?> get() = _pickedLatLng

    private val _pickedName = MutableStateFlow("")
    val pickedName: StateFlow<String> get() = _pickedName

    private val _addState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val addState: StateFlow<UiState<Unit>> get() = _addState

    init {
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
                    address?.locality     != null -> address.locality
                    address?.subAdminArea != null -> address.subAdminArea
                    address?.adminArea    != null -> address.adminArea
                    else                          -> "Pinned Location"
                }
                withContext(Dispatchers.Main) {
                    if (_pickedLatLng.value == latLng) {
                        _pickedName.value = name
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    fun clearPin() {
        _pickedLatLng.value = null
        _pickedName.value   = ""
        _addState.value     = UiState.Idle
    }
    fun zoomIn(cameraState: CameraPositionState) {
        viewModelScope.launch {
            val currentZoom = cameraState.position.zoom
            cameraState.animate(
                update     = CameraUpdateFactory.zoomTo(currentZoom + 1f),
                durationMs = 300
            )
        }
    }

    fun zoomOut(cameraState: CameraPositionState) {
        viewModelScope.launch {
            val currentZoom = cameraState.position.zoom
            cameraState.animate(
                update     = CameraUpdateFactory.zoomTo(currentZoom - 1f),
                durationMs = 300
            )
        }
    }

    fun onLocationPicked(
        lat: Double,
        lng: Double,
        name: String,
        favouriteViewModel: FavouriteViewModel,
        apiKey: String = "3ec08632a7a945e6408e9414cd1fab66"
    ) {
        viewModelScope.launch {
            _addState.value = UiState.Loading
            try {
                val geocoder  = Geocoder(app, Locale.getDefault())
                var cityName    = name
                var countryName = ""
                var countryCode = ""

                launch(Dispatchers.IO) {
                    try {
                        val addresses = geocoder.getFromLocation(lat, lng, 1)
                        val address   = addresses?.firstOrNull()
                        cityName    = address?.locality    ?: name
                        countryName = address?.countryName ?: ""
                        countryCode = address?.countryCode ?: ""
                    } catch (e: Exception) {

                    }
                }.join()


                val response = weatherRepository.getOneCallResponse(lat, lng, apiKey)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {

                        val item = FavouriteEntity(
                            lat             = body.lat,
                            lon             = body.lon,
                            cityName        = cityName,
                            countryName     = countryName,
                            countryCode     = countryCode,
                            oneCallResponse = body
                        )
                        favouriteViewModel.addFavourite(item)
                        _addState.value = UiState.Success(Unit)
                    } else {
                        _addState.value = UiState.Error("Empty response")
                    }
                }else {
                    _addState.value = UiState.Error("Error ${response.code()}: ${response.message()}")
                }

            } catch (ex: Exception) {
                _addState.value = UiState.Error(ex.message ?: "Unknown error")
            }
        }
    }

    private fun Double.toCelsius() = (this - 273.15).toInt()
}

class MapPickerViewModelFactory(
    private val app: Application,
    private val weatherRepository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapPickerViewModel(app, weatherRepository) as T
    }
}