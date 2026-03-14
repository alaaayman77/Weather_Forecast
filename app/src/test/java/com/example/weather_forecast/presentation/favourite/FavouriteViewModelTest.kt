package com.example.weather_forecast.presentation.favourite

import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.CurrentWeather
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.OneCallResponse
import com.example.weather_forecast.data.models.WeatherCondition
import com.example.weather_forecast.presentation.weather.FavouriteState
import com.example.weather_forecast.presentation.weather.UiState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var fav1: FavouriteEntity
    private lateinit var fav2: FavouriteEntity

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val fakeWeather = OneCallResponse(
            lat = 30.0, lon = 31.0,
            timezone = "Africa/Cairo", timezone_offset = 7200,
            current = CurrentWeather(
                dt = 1710000000L, sunrise = 1709990000L, sunset = 1710030000L,
                temp = 25.0, feels_like = 24.0, pressure = 1013, humidity = 50,
                dew_point = 14.0, uvi = 5.0, clouds = 10, visibility = 10000,
                wind_speed = 3.5, wind_deg = 120, wind_gust = null,
                weather = listOf(WeatherCondition(800, "Clear", "clear sky", "01d"))
            ),
            hourly = emptyList(), daily = emptyList(), alerts = emptyList()
        )

        fav1 = FavouriteEntity(30.0, 31.0, "Cairo",      "Egypt", "EG", fakeWeather)
        fav2 = FavouriteEntity(29.9, 31.2, "Alexandria", "Egypt", "EG", fakeWeather)

        repository = mockk(relaxed = true)
        every { repository.getAllFavourites() } returns flowOf(listOf(fav1))

        viewModel = FavouriteViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadFavourites_onInit_uiStateHasCorrectList() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Success<FavouriteState>

        assertThat(state.data.favourites, `is`(listOf(fav1)))
    }

    @Test
    fun addFavourite_callsRepository() = runTest {
        viewModel.addFavourite(fav2)
        advanceUntilIdle()

        coVerify { repository.addFavourite(fav2) }
    }

    @Test
    fun removeFavourite_callsRepositoryWithCorrectCoordinates() = runTest {
        viewModel.removeFavourite(30.0, 31.0)
        advanceUntilIdle()

        coVerify { repository.removeFavourite(30.0, 31.0) }
    }
}