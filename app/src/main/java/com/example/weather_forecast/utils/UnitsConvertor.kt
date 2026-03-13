import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit


object UnitConverter {

    fun convertTemp(celsius: Double, unit: TempUnit): Int =
        when (unit) {
        TempUnit.CELSIUS    -> celsius.toInt()
        TempUnit.FAHRENHEIT -> (celsius * 9.0 / 5.0 + 32.0).toInt()
        TempUnit.KELVIN     -> (celsius + 273.15).toInt()
    }

    fun tempSymbol(unit: TempUnit): String =
        when (unit) {
        TempUnit.CELSIUS    -> "°C"
        TempUnit.FAHRENHEIT -> "°F"
        TempUnit.KELVIN     -> "K"
    }
    fun convertWind(ms: Double, unit: WindUnit): String =
        when (unit) {
        WindUnit.MS  -> "%.1f m/s".format(ms)
        WindUnit.MPH -> "%.1f mph".format(ms * 2.23694)
        WindUnit.KMH -> "%.1f km/h".format(ms * 3.6)
    }
}