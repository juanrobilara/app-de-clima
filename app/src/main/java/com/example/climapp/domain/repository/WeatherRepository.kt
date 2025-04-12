package com.example.climapp.domain.repository

import com.example.climapp.data.network.WeatherApiService
import com.example.climapp.domain.model.WeatherResponse
import javax.inject.Inject

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
}