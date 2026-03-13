package com.example.weather_forecast.utils

import android.content.Context
import com.example.weather_forecast.data.models.Language
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
