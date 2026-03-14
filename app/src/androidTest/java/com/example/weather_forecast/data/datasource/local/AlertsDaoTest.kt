package com.example.weather_forecast.data.datasource.local


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import androidx.test.runner.AndroidJUnit4
import com.example.weather_forecast.data.datasource.db.AppDatabase
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.AlertMode
import com.example.weather_forecast.data.models.AlertType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AlertsDaoTest {

    lateinit var dao     : AlertsDao
    lateinit var database: AppDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.alertsDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAlert_andGetAll_returnsSameAlert() = runTest {
        val alert = AlertEntity(
            id           = 1,
            label        = "Storm warning",
            startMillis  = 1000L,
            endMillis    = 2000L,
            status       = AlertStatus.SCHEDULED,
            mode         = AlertMode.CUSTOM,
            type         = AlertType.NOTIFICATION
        )

        dao.insertAlert(alert)

        val result = dao.getAllAlerts().first()

        assertThat(result.first(), `is`(alert))
    }

    @Test
    fun updateStatus_andGetAll_returnsUpdatedStatus() = runTest {
        val alert = AlertEntity(
            id           = 1,
            label        = "Storm warning",
            startMillis  = 1000L,
            endMillis    = 2000L,
            status       = AlertStatus.SCHEDULED,
            mode         = AlertMode.CUSTOM,
            type         = AlertType.NOTIFICATION
        )
        dao.insertAlert(alert)

        dao.updateStatus(alert.id, AlertStatus.ACTIVE)

        val result = dao.getAllAlerts().first()

        assertThat(result.first().status, `is`(AlertStatus.ACTIVE))
    }

    @Test
    fun deleteAlert_andGetAll_returnsEmptyList() = runTest {
        val alert = AlertEntity(
            id           = 1,
            label        = "Storm warning",
            startMillis  = 1000L,
            endMillis    = 2000L,
            status       = AlertStatus.SCHEDULED,
            mode         = AlertMode.CUSTOM,
            type         = AlertType.NOTIFICATION
        )
        dao.insertAlert(alert)

        dao.deleteAlert(alert.id)

        val result = dao.getAllAlerts().first()

        assertThat(result.isEmpty(), `is`(true))
    }
}