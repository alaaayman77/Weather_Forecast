package com.example.weather_forecast.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {

        @Query("SELECT * FROM alerts ORDER BY startMillis ASC")
        fun getAllAlerts(): Flow<List<AlertEntity>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAlert(alert: AlertEntity)

        @Query("UPDATE alerts SET status = :status WHERE id = :alertId")
        suspend fun updateStatus(alertId: Int, status: AlertStatus)

        @Query("DELETE FROM alerts WHERE id = :alertId")
        suspend fun deleteAlert(alertId: Int)

}