package com.example.climapp.data.network

import com.example.climapp.domain.model.WeatherResponse
import com.example.climapp.domain.repository.NearbyWeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://api.openweathermap.org/"

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") locationKey: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
    ): WeatherResponse

    @GET("data/2.5/find")
    suspend fun getNearbyWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") cnt: Int = 20,
        @Query("appid") appid: String,
        @Query("units") units: String = "metric"
    ): NearbyWeatherResponse


    companion object {
        fun create(): WeatherApiService {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client =
                OkHttpClient
                    .Builder()
                    .addInterceptor(logging)
                    .build()

            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(WeatherApiService::class.java)
        }
    }
}