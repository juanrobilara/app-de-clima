package com.example.climapp.domain.model

import com.google.gson.annotations.SerializedName

const val KELVIN: Double = 273.0

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain?,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int,
)

data class Coord(
    val lon: Double,
    val lat: Double,
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("sea_level")
    val seaLevel: Int?,
    @SerializedName("grnd_level")
    val grndLevel: Int?,
)

fun Double.toCelsius(): Double = (this - KELVIN)

fun translateCondition(condition: String): String =
    when (condition) {
        "Clear" -> "Despejado"
        "Clouds" -> "Nublado"
        "Rain" -> "Lluvia"
        "Drizzle" -> "Llovizna"
        "Thunderstorm" -> "Tormenta"
        "Snow" -> "Nieve"
        "Mist" -> "Niebla"
        "Smoke" -> "Humo"
        "Haze" -> "Neblina"
        "Dust" -> "Polvo"
        "Fog" -> "Niebla densa"
        "Sand" -> "Tormenta de arena"
        "Ash" -> "Ceniza volcánica"
        "Squall" -> "Ráfagas"
        "Tornado" -> "Tornado"
        else -> condition
    }

fun windDirectionFromDegrees(deg: Int): String {
    val directions = listOf(
        "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
        "S", "SS0", "SO", "OSO", "W", "ONO", "NO", "NNO"
    )
    val index = ((deg / 22.5) + 0.5).toInt() % 16
    return directions[index]
}


fun windSpeedToKilometers(speed: Double): Double =
    speed * 1.609


data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double?,
)

data class Rain(
    @SerializedName("1h")
    val oneHour: Double,
)

data class Clouds(
    val all: Int,
)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
)