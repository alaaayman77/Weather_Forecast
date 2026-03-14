package com.example.weather_forecast.data.models

enum class CustomCondition(
    val label      : String,
    val description: String,
    val emoji      : String,
    val owmIdRange : List<IntRange>
) {
    THUNDERSTORM(
        "Thunderstorm", "Any thunderstorm condition", "⛈️", listOf(200..232)
    ),
    DRIZZLE(
        "Drizzle", "Any drizzle condition", "🌦️", listOf(300..321)
    ),
    RAIN_LIGHT(
        "Light Rain", "Light or moderate rain", "🌧️", listOf(500..501)
    ),
    RAIN_HEAVY(
        "Heavy Rain", "Heavy or extreme rain", "🌧️", listOf(502..531)
    ),
    RAIN_FREEZING(
        "Freezing Rain", "Freezing rain", "🌨️", listOf(511..511)
    ),
    SNOW(
        "Snow", "Any snow condition", "❄️", listOf(600..622)
    ),
    SLEET(
        "Sleet / Mixed", "Sleet or rain and snow mix", "🌨️", listOf(611..616)
    ),
    FOG_MIST(
        "Fog / Mist / Haze", "Low visibility conditions", "🌫️", listOf(701..741)
    ),
    DUST_SAND(
        "Dust / Sand", "Dust or sand in the air", "🌪️", listOf(731..761)
    ),
    TORNADO_SQUALL(
        "Tornado / Squall", "Severe wind events", "🌪️", listOf(771..781)
    ),
    CLEAR_SKY(
        "Clear Sky", "Clear sky", "☀️", listOf(800..800)
    ),
    PARTLY_CLOUDY(
        "Partly Cloudy", "Few or scattered clouds", "⛅", listOf(801..802)
    ),
    CLOUDY(
        "Cloudy / Overcast", "Broken or overcast clouds", "☁️", listOf(803..804)
    ),
    HIGH_TEMP(
        "High Temp > 40°", "Temperature exceeds 40°C", "🌡️", emptyList()
    ),
    LOW_TEMP(
        "Low Temp < 0°", "Temperature drops below 0°C", "🥶", emptyList()
    );

    fun matches(owmId: Int, tempKelvin: Double): Boolean {
        val tempCelsius = tempKelvin - 273.15
        if (this == HIGH_TEMP) return tempCelsius >= 40.0
        if (this == LOW_TEMP)  return tempCelsius <= 0.0
        return owmIdRange.any { owmId in it }
    }
}