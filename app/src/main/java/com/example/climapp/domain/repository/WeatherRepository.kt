package com.example.climapp.domain.repository

import com.example.climapp.data.network.WeatherApiService
import com.example.climapp.domain.model.WeatherResponse
import com.example.climapp.domain.model.toCelsius
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherRepository
@Inject
constructor(
    private val apiService: WeatherApiService,
) {
    suspend fun getCurrentWeather(
        lat: String,
        long: String,
        appid: String,
    ): WeatherResponse = apiService.getCurrentWeather(lat, long, appid)


    //implementar a futuro en el mapa para actualización dinámica
    suspend fun getNearbyWeather(
        lat: Double,
        lon: Double,
        count: Int = 20,
        appid: String
    ): List<WeatherPoint> {
        val response = apiService.getNearbyWeather(
            lat = lat,
            lon = lon,
            cnt = count,
            appid = appid,
            units = "metric"
        )

        return response.list.map {
            WeatherPoint(
                lat = it.coord.lat,
                lon = it.coord.lon,
                temp = it.main.temp.toCelsius().roundToInt()
            )
        }
    }

}

data class WeatherPoint(
    val lat: Double,
    val lon: Double,
    val temp: Int
)

data class NearbyWeatherResponse(
    val list: List<NearbyCityWeather>
)

data class NearbyCityWeather(
    val name: String,
    val coord: Coord,
    val main: Main
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class Main(
    val temp: Double
)