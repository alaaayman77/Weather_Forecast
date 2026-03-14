package com.example.weather_forecast.data

import com.example.weather_forecast.data.datasource.local.FakeLocalDataSource
import com.example.weather_forecast.data.datasource.remote.FakeRemoteDataSource
import com.example.weather_forecast.data.models.CurrentWeather
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.OneCallResponse
import com.example.weather_forecast.data.models.WeatherCondition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var fakeLocal: FakeLocalDataSource
    private lateinit var fakeRemote: FakeRemoteDataSource
    private lateinit var repository: WeatherRepository

    private lateinit var fav1: FavouriteEntity
    private lateinit var fav2: FavouriteEntity

    @Before
    fun setup() {
        val fakeWeather = OneCallResponse(
            lat = 30.0,
            lon = 31.0,
            timezone = "Africa/Cairo",
            timezone_offset = 7200,
            current = CurrentWeather(
                dt = 1710000000L,
                sunrise = 1709990000L,
                sunset = 1710030000L,
                temp = 25.0,
                feels_like = 24.0,
                pressure = 1013,
                humidity = 50,
                dew_point = 14.0,
                uvi = 5.0,
                clouds = 10,
                visibility = 10000,
                wind_speed = 3.5,
                wind_deg = 120,
                wind_gust = null,
                weather = listOf(
                    WeatherCondition(
                        id = 800,
                        main = "Clear",
                        description = "clear sky",
                        icon = "01d"
                    )
                )
            ),
            hourly = emptyList(),
            daily = emptyList(),
            alerts = emptyList()
        )

        fav1 = FavouriteEntity(
            lat = 30.0,
            lon = 31.0,
            cityName = "Cairo",
            countryName = "Egypt",
            countryCode = "EG",
            oneCallResponse = fakeWeather
        )

        fav2 = FavouriteEntity(
            lat = 29.9,
            lon = 31.2,
            cityName = "Alexandria",
            countryName = "Egypt",
            countryCode = "EG",
            oneCallResponse = fakeWeather
        )

        fakeLocal  = FakeLocalDataSource(mutableListOf(fav1))
        fakeRemote = FakeRemoteDataSource()

        repository = WeatherRepository(fakeRemote, fakeLocal)
    }

    @Test
    fun getAllFavourites_fav1_returnsLocalData() = runTest {
        val result = repository.getAllFavourites().first()

        assertThat(result, `is`(listOf(fav1)))
    }

    @Test
    fun addFavourite_fav2_itemAdded() = runTest {
        repository.addFavourite(fav2)

        val result = repository.getAllFavourites().first()

        assertThat(result.contains(fav2), `is`(true))
    }

    @Test
    fun removeFavourite_fav1_itemRemoved() = runTest {
        repository.removeFavourite(30.0, 31.0)

        val result = repository.getAllFavourites().first()

        assertThat(result.contains(fav1), `is`(false))
    }
}