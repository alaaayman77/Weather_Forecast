package com.example.weather_forecast.presentation.settings

import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.datasource.local.FakeLocalDataSource
import com.example.weather_forecast.data.datasource.remote.FakeRemoteDataSource
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        val repository = WeatherRepository(FakeRemoteDataSource(), FakeLocalDataSource())
        viewModel = SettingsViewModel(repository)
    }

    @Test
    fun setTempUnit_updatesStateFlow() {
        viewModel.setTempUnit(TempUnit.FAHRENHEIT)

        assertThat(viewModel.tempUnit.value, `is`(TempUnit.FAHRENHEIT))
    }

    @Test
    fun setWindUnit_updatesStateFlow() {
        viewModel.setWindUnit(WindUnit.MPH)

        assertThat(viewModel.windUnit.value, `is`(WindUnit.MPH))
    }

    @Test
    fun setLanguage_updatesStateFlow() {
        viewModel.setLanguage(Language.ARABIC)

        assertThat(viewModel.language.value, `is`(Language.ARABIC))
    }
}