package com.example.weather_forecast.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast.data.WeatherRepository


class OnBoardingViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    fun isOnboarded(): Boolean = repository.isOnboarded()

    fun setOnboarded() = repository.saveOnboarded(true)
}

class OnBoardingViewModelFactory(

    private val weatherRepository: WeatherRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        OnBoardingViewModel( weatherRepository, ) as T
}