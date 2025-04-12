package com.example.climapp.data.di

import com.example.climapp.data.network.WeatherApiService
import com.example.climapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideWeatherApiService(): WeatherApiService = WeatherApiService.create()

    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: WeatherApiService): WeatherRepository = WeatherRepository(apiService)

}