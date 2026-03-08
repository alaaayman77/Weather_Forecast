package com.example.weather_forecast.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_forecast.data.models.FavouriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): Flow<List<FavouriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE lat = :lat AND lon = :lon")
    suspend fun deleteFavouriteByLatLon(lat: Double, lon: Double)

    @Query("SELECT * FROM favourites WHERE lat = :lat AND lon = :lon")
    suspend fun getFavouriteByLatLon(lat: Double, lon: Double): FavouriteEntity?
}