package com.example.weather_forecast.utils

import android.content.Context
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import java.util.Locale



fun getApiLangCode(language: Language): String= when (language) {
    Language.ENGLISH -> "en"
    Language.ARABIC  -> "ar"

}

fun getLocale(language: Language): Locale = when(language) {
    Language.ENGLISH -> Locale.ENGLISH
    Language.ARABIC  -> Locale("ar")

}
fun getDisplayName(language: Language): String = when (language) {
    Language.ENGLISH -> "English (US)"
    Language.ARABIC  -> "Arabic"
}
private val arabicDigits = charArrayOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
fun formatNumber(number: String, language: Language): String {
    if (language != Language.ARABIC) return number
    return number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}
fun formatNumber(number: Int, language: Language): String =
    formatNumber(number.toString(), language)

fun formatNumber(number: Double, language: Language): String =
    formatNumber(number.toString(), language)


fun formatTemp(value: Int, unit: TempUnit, language: Language): String {
    val number = formatNumber(value, language)
    val symbol = UnitConverter.tempSymbol(unit)
    return "$number$symbol"
}



fun formatWind(ms: Double, unit: WindUnit, language: Language): String {
    val (value, label) = when (unit) {
        WindUnit.MS  -> "%.1f".format(ms)          to "m/s"
        WindUnit.MPH -> "%.1f".format(ms * 2.23694) to "mph"
        WindUnit.KMH -> "%.1f".format(ms * 3.6)     to "km/h"
    }
    val number = formatNumber(value, language)
    return if (language == Language.ARABIC) "$label $number" else "$number $label"
}
fun applyLocale(context: Context, language: Language) {
    val locale = getLocale(language)
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
