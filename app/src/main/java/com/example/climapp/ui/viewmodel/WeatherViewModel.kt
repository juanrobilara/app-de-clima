package com.example.climapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climapp.BuildConfig
import com.example.climapp.data.local.CityDao
import com.example.climapp.data.local.CityEntity
import com.example.climapp.data.network.CityApiService
import com.example.climapp.domain.model.City
import com.example.climapp.domain.repository.WeatherRepository
import com.example.climapp.ui.core.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val geoDbService: CityApiService,
    private val cityDao: CityDao
) : ViewModel() {

    private val key = BuildConfig.API_KEY
    private val searchQuery = MutableStateFlow("")
    private val _citySuggestions = MutableStateFlow<List<City>>(emptyList())
    val citySuggestions: StateFlow<List<City>> = _citySuggestions.asStateFlow()
    val savedCities: StateFlow<List<CityEntity>> = cityDao.getAllCities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _cityWeatherMap = MutableStateFlow<Map<Int, WeatherUiState>>(emptyMap())
    val cityWeatherMap: StateFlow<Map<Int, WeatherUiState>> = _cityWeatherMap.asStateFlow()

    init {
        savedCities.onEach { cities ->
            fetchWeatherForSavedCities(cities)
        }.launchIn(viewModelScope)
        observeSearchQuery()
    }

    private fun fetchWeatherForSavedCities(cities: List<CityEntity>) {
        viewModelScope.launch {
            val updatedMap = ConcurrentHashMap(_cityWeatherMap.value)

            cities.forEach { city ->
                if (updatedMap[city.id] !is WeatherUiState.Success) {
                    try {
                        updatedMap[city.id] = WeatherUiState.Loading
                        _cityWeatherMap.value = updatedMap.toMap()

                        val response = repository.getCurrentWeather(city.lat.toString(), city.lon.toString(), key)
                        updatedMap[city.id] = WeatherUiState.Success(response)
                    } catch (e: Exception) {
                        Log.e("WeatherViewModel", "Error obteniendo clima de ${city.name}", e)
                        updatedMap[city.id] = WeatherUiState.Error("Error")
                    }
                }
            }
            _cityWeatherMap.value = updatedMap.toMap()
        }
    }


    @OptIn(FlowPreview::class)
    fun observeSearchQuery() {
        viewModelScope.launch {
            var lastQuery = ""
            searchQuery
                .debounce(500)
                .filter { it.isNotBlank() && it.length > 2 }
                .distinctUntilChanged()
                .collect { query ->
                    if (query != lastQuery) {
                        getSuggestions(query)
                        lastQuery = query
                    }
                }
        }
    }

    private suspend fun getSuggestions(query: String) {
        try {
            val response = geoDbService.getCities(query)
            _citySuggestions.value = response.data
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Error obteniendo sugerencias", e)
        }
    }

    fun clearSuggestions() {
        _citySuggestions.value = emptyList()
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun addSavedCity(city: City) {
        viewModelScope.launch {
            val cityEntity = CityEntity(
                id = city.id,
                name = city.name,
                lat = city.latitude,
                lon = city.longitude
            )
            cityDao.insertCity(cityEntity)
        }
    }

    // impl proximamente para borrar
    fun deleteCity(city: CityEntity) {
        viewModelScope.launch {
            cityDao.deleteCity(city)
            _cityWeatherMap.value = _cityWeatherMap.value.toMutableMap().apply {
                remove(city.id)
            }
        }
    }

    fun refreshWeatherForCity(city: CityEntity) {
        viewModelScope.launch {
            _cityWeatherMap.value = _cityWeatherMap.value.toMutableMap().apply {
                put(city.id, WeatherUiState.Loading)
            }
            try {
                val response = repository.getCurrentWeather(city.lat.toString(), city.lon.toString(), key)
                _cityWeatherMap.value = _cityWeatherMap.value.toMutableMap().apply {
                    put(city.id, WeatherUiState.Success(response))
                }
            } catch (e: Exception) {
                _cityWeatherMap.value = _cityWeatherMap.value.toMutableMap().apply {
                    put(city.id, WeatherUiState.Error("Error"))
                }
            }
        }
    }
}
