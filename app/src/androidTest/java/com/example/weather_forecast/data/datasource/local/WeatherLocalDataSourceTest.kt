package com.example.weather_forecast.data.datasource.local


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import com.example.weather_forecast.data.datasource.db.AppDatabase
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertMode
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.data.models.CurrentWeather
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.OneCallResponse
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WeatherCondition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceTest {

    lateinit var database       : AppDatabase
    lateinit var localDataSource: WeatherLocalDataSource
    lateinit var fakeWeather : OneCallResponse

    @Before
    fun setup() {
         fakeWeather = OneCallResponse(
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
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        localDataSource = WeatherLocalDataSource(ApplicationProvider.getApplicationContext())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun addFavourite_andGetAll_returnsSavedFavourite() = runTest {
        val fav = FavouriteEntity(30.0, 31.0, "Cairo", "Egypt", "EG", fakeWeather)

        localDataSource.addFavourite(fav)

        val result = localDataSource.getAllFavourites().first()

        assertThat(result.contains(fav), `is`(true))
    }

    @Test
    fun removeFavourite_andGetAll_returnsEmptyList() = runTest {
        val fav = FavouriteEntity(30.0, 31.0, "Cairo", "Egypt", "EG", fakeWeather)

        localDataSource.addFavourite(fav)
        localDataSource.removeFavourite(30.0, 31.0)

        val result = localDataSource.getAllFavourites().first()

        assertThat(result.contains(fav), `is`(false))
    }

    @Test
    fun saveTempUnit_andGetTempUnit_returnsUpdatedUnit() {
        localDataSource.saveTempUnit(TempUnit.FAHRENHEIT)

        val result = localDataSource.getTempUnit()

        assertThat(result, `is`(TempUnit.FAHRENHEIT))
    }

    @Test
    fun saveLanguage_andGetLanguage_returnsUpdatedLanguage() {
        localDataSource.saveLanguage(Language.ARABIC)

        val result = localDataSource.getLanguage()

        assertThat(result, `is`(Language.ARABIC))
    }

    @Test
    fun insertAlert_andGetAll_returnsSavedAlert() = runTest {
        val alert = AlertEntity(
            id          = 1,
            label       = "Storm warning",
            startMillis = 1000L,
            endMillis   = 2000L,
            status      = AlertStatus.SCHEDULED,
            mode        = AlertMode.CUSTOM,
            type        = AlertType.NOTIFICATION
        )

        localDataSource.insertAlert(alert)

        val result = localDataSource.getAllAlerts().first()

        assertThat(result.contains(alert), `is`(true))
    }
}