# 🌤️ Weather Forecast App

A modern Android weather application built with Kotlin and Jetpack Compose, featuring real-time weather data, custom alerts, favorites, and full localization support.

---

## 📸 Screenshots

> _Add your screenshots here_

---

## ✨ Features

### 🌡️ Weather Data
- Current weather conditions with animated GIF/video backgrounds that match the weather
- Hourly forecast (next 48 hours)
- 7-day weekly forecast with temperature range bars
- Detailed weather stats: humidity, wind speed, pressure, cloud cover, UV index, sunrise/sunset
- Feels like temperature and dew point
- Offline support — caches last fetched weather and shows an offline banner

### 📍 Location
- **GPS mode** — automatically fetches weather for your current location
- **Map mode** — manually pick any location on a Google Maps picker
- Reverse geocoding for human-readable location names 

### 🔔 Alerts
- Schedule weather alerts with start and end times
- Two alert modes:
  - **OWM Alerts** — official weather alerts from OpenWeatherMap
  - **Custom Alerts** — user-defined conditions:
    - Thunderstorm, Drizzle, Rain (light/heavy/freezing), Snow, Sleet
    - Fog/Mist/Haze, Dust/Sand, Tornado/Squall
    - Clear Sky, Partly Cloudy, Cloudy/Overcast
    - High Temp (> 40°C) and Low Temp (< 0°C)
- Foreground notification service delivers alerts even when the app is in the background
- Exact alarm scheduling with `AlarmManager`

### ⭐ Favourites
- Save multiple locations as favourites
- View full weather details for any saved location
- Add locations via the map picker

### ⚙️ Settings
- **Temperature unit**: Celsius, Fahrenheit, Kelvin
- **Wind speed unit**: m/s, mph, km/h
- **Location source**: GPS or manual map pick
- **Language**: English and Arabic (with full RTL support)

### 🌐 Localization
- Full Arabic and English support
- Numbers and text formatted per locale
- RTL layout for Arabic
- Language change triggers app recreation for instant effect

---

## 🏗️ Architecture

The app follows **MVVM (Model-View-ViewModel)** with a clean layered architecture:

```
app/
├── data/
│   ├── datasource/
│   │   ├── local/          # SharedPreferences + Room database
│   │   └── remote/         # Retrofit API calls
│   ├── models/             # Data classes, enums (TempUnit, Language, etc.)
│   └── WeatherRepository   # Single source of truth
├── presentation/
│   ├── weather/            # Main weather screen + ViewModel
│   ├── alerts/             # Alert scheduling + notification service
│   ├── favourite/          # Favourites list + map picker
│   ├── favouriteDetails/   # Weather detail for a saved location
│   ├── settings/           # Settings screen + ViewModel
│   ├── onboarding/         # First-launch onboarding
│   └── permission/         # Location permission handling
└── utils/                  # Location provider, formatters, converters
```

### Key Architectural Decisions

- **StateFlow** for all UI state — collected with `collectAsStateWithLifecycle`
- **SharedFlow** for one-shot events (e.g. open location settings dialog)
- **Room** for persistent alert storage
- **SharedPreferences** for user settings and cached weather JSON
- **Gson** for serializing/deserializing `OneCallResponse` to/from cache
- **Retrofit** for all network calls
- Single `WeatherRepository` wraps both local and remote data sources

---

## 🛠️ Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Navigation | Compose Navigation |
| Async | Kotlin Coroutines, StateFlow |
| Networking | Retrofit 2, OkHttp |
| JSON | Gson |
| Database | Room |
| Maps | Google Maps Compose |
| Images | Coil |
| Location | Google Play Services Location |
| Notifications | Android NotificationManager, AlarmManager |
| DI | Manual (ViewModelFactory) |

---

## 🌐 API

This app uses the **OpenWeatherMap One Call API 3.0**.

- Base URL: `https://api.openweathermap.org/data/3.0/`
- Endpoint: `GET /onecall`
- Parameters: `lat`, `lon`, `appid`, `units=metric`, `lang`, `exclude=minutely`

> All temperatures from the API are returned in **Celsius** when `units=metric` is set. The app then converts to the user's chosen unit (Celsius / Fahrenheit / Kelvin) in the UI layer via `UnitConverter`.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 26+
- A valid [OpenWeatherMap API key](https://openweathermap.org/api)
- A valid [Google Maps API key](https://developers.google.com/maps)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/weather-forecast.git
   cd weather-forecast
   ```

2. **Add your API keys**

   In your `local.properties` file (create it if it doesn't exist):
   ```properties
   OPEN_WEATHER_API_KEY=your_openweathermap_api_key_here
   MAPS_API_KEY=your_google_maps_api_key_here
   ```

   In your `AndroidManifest.xml`, the Maps key is injected via:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${MAPS_API_KEY}" />
   ```

   In your `build.gradle` (app level), the OWM key is exposed via `BuildConfig`:
   ```groovy
   buildConfigField "String", "OPEN_WEATHER_API_KEY", "\"${localProperties['OPEN_WEATHER_API_KEY']}\""
   ```

3. **Build and run**

   Open the project in Android Studio and click **Run**, or:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📁 Project Structure Highlights

```
presentation/weather/view/
├── WeatherScreen.kt          # Main screen composable
├── WeatherScreenContent      # LazyColumn layout
├── WeatherCenterSection      # Big temp display, H/L pills, feels like
├── WeatherTopBar             # Greeting, date/time, location chip
├── WeatherInfoGrid           # Humidity, wind, pressure, clouds, sunrise, sunset
├── HourlyForecastList        # Horizontal hourly scroll
├── WeeklyList                # 7-day forecast with temp range bars
└── components/
    ├── WeatherVideoBackground # Weather-reactive video/gif background
    ├── SunTimesCard           # Sunrise/sunset with arc progress
    ├── UvIndexCard            # UV index with color scale
    ├── WeatherInfoCard        # Reusable stat card
    ├── HourlyForecastItem     # Single hourly chip
    └── WeeklyForecastItem     # Single daily row with bar
```

## 🗺️ Offline Support

When a network request fails (IOException or non-2xx response), the app:
1. Attempts to load the last cached `OneCallResponse` from SharedPreferences (stored as JSON)
2. If cache exists → shows weather with an `OfflineBanner` overlay
3. If no cache → shows an error state with the failure message


## 📬 Contact

Made with Kotlin. If you find a bug or have a feature request, open an issue on GitHub.
