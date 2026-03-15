//package com.example.weather_forecast.presentation.weather.components
//
//import com.example.weather_forecast.R
//import java.util.Calendar
//
//fun getWeatherBackground(weatherId: Int?, dt: Long?): Int {
//    val hour = if (dt != null) {
//        val cal = Calendar.getInstance()
//        cal.timeInMillis = dt * 1000
//        cal.get(Calendar.HOUR_OF_DAY)
//    } else {
//        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
//    }
//    val isNight = hour < 6 || hour >= 20
//
//    return when (weatherId) {
//        // ☀️ Clear / Sunny
//        800       -> if (isNight) R.raw.clear_night  else R.raw.rainy_night
//
//         //⛅ Cloudy
//        801, 802,
//        803, 804  -> if (isNight) R.raw.cloudy_night else R.raw.cloudy_night
//
//        // 🌧️ Rain + Drizzle
//        in 300..399,
//        in 500..599 -> if (isNight) R.raw.rainy_night else R.raw.rainy_day
//
//        // ⛈️ Thunderstorm
//        in 200..299 -> R.raw.thunderstorm
//
//        // 🌨️ Snow
//        in 600..699 -> R.raw.snowy
//
//        // 🌫️ Mist / Fog / Haze
//        in 700..799 -> R.raw.haze
//
//        // default — fallback to sunny
//        else        -> if (isNight) R.raw.clear_night else R.drawable.sunny_day
//    }
//}