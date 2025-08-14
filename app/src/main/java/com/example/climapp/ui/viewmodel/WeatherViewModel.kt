package com.example.climapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climapp.BuildConfig
import com.example.climapp.data.local.CityDao
import com.example.climapp.data.local.CityEntity
import com.example.climapp.data.network.CityApiService
import com.example.climapp.domain.model.City
import com.example.climapp.domain.model.WeatherResponse
import com.example.climapp.domain.model.toCelsius
import com.example.climapp.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val geoDbService: CityApiService,
    private val cityDao: CityDao
) : ViewModel() {





    // === CLIMA ===
    private val _currentWeather = MutableStateFlow<WeatherResponse?>(null)
    val currentWeather: StateFlow<WeatherResponse?> = _currentWeather.asStateFlow()
    private val key = BuildConfig.API_KEY
    private val searchQuery = MutableStateFlow("")

//Inicio para evitar problemas
    init {
        fetchCurrentWeather()
        observeSearchQuery()
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

// === BÚSQUEDA ===
    private val _citySuggestions = MutableStateFlow<List<City>>(emptyList())
    val citySuggestions: StateFlow<List<City>> = _citySuggestions.asStateFlow()
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
            Log.e("WeatherViewModel", "Error fetching city suggestions", e)
        }
    }

    fun clearSuggestions() {
        _citySuggestions.value = emptyList()
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    // === HISTORIAL (En proceso) ===
    fun getHistory(): Flow<List<CityEntity>> = cityDao.getRecentCities()

    private suspend fun saveToHistory(city: City) {
        cityDao.insert(
            CityEntity(name = city.name, lat = city.latitude, lon = city.longitude)
        )
    }
}

