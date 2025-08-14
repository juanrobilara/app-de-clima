package com.example.climapp.data.network

import com.example.climapp.BuildConfig
import com.example.climapp.domain.model.CityResponse
import com.example.climapp.domain.model.WeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://wft-geo-db.p.rapidapi.com/"

interface CityApiService {



    @GET("v1/geo/cities")
    suspend fun getCities(
        @Query("namePrefix") namePrefix: String,
        @Query("limit") limit: Int = 5,
        @Query("languageCode") languageCode: String = "es"
    ): CityResponse

    companion object {


        fun create(): CityApiService {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-RapidAPI-Key", BuildConfig.RAPID_API_KEY)
                        .addHeader("X-RapidAPI-Host", "wft-geo-db.p.rapidapi.com")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(logging)
                .build()

            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(CityApiService::class.java)
        }
    }
}

