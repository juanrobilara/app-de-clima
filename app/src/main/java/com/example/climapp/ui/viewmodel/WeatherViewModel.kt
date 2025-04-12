package com.example.climapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climapp.BuildConfig
import com.example.climapp.domain.model.WeatherResponse
import com.example.climapp.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _currentWeather = MutableStateFlow<WeatherResponse?>(null)
    val currentWeather: StateFlow<WeatherResponse?> = _currentWeather.asStateFlow()
    private val key = BuildConfig.API_KEY
    init {
        fetchCurrentWeather()
    }

    fun fetchCurrentWeather(lat: String = "-34.669635", lon: String = "-58.564624") {
        viewModelScope.launch {
            try {
                val response = repository.getCurrentWeather(lat, lon, key)
                _currentWeather.value = response
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
            }
        }
    }
}
