package com.example.weather_forecast.data.network


import com.example.weather_forecast.data.models.OneCallResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

        @GET("onecall")
        suspend fun getOneCallResponse(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") apiKey: String,
            @Query("exclude") exclude: String = "minutely"
        ): Response<OneCallResponse>


}
