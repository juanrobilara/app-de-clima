package com.example.climapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climapp.BuildConfig
import com.example.climapp.domain.model.toCelsius
import com.example.climapp.domain.repository.WeatherPoint
import com.example.climapp.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import javax.inject.Inject
import org.maplibre.android.maps.MapLibreMap
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@HiltViewModel
class WeatherMapViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherPoints = MutableStateFlow<List<WeatherPoint>>(emptyList())
    val weatherPoints = _weatherPoints.asStateFlow()
    val key = BuildConfig.API_KEY
    private var mapLibreMap: MapLibreMap? = null

//Consumir la API con las coordenadas
    fun fetchArgentineCapitalsWeather() {
        viewModelScope.launch {
            val capitals = getArgentineCapitals()
            val result = mutableListOf<WeatherPoint>()

            for ((name, coord) in capitals) {
                try {
                    val weather = repository.getCurrentWeather(coord.first.toString(), coord.second.toString(), key)
                    val temp = weather.main.temp.toCelsius().roundToInt()
                    result.add(WeatherPoint(coord.first, coord.second, temp))
                } catch (e: Exception) {
                    Log.e("WeatherMapVM", "Error fetching weather for $name", e)
                }
            }

            _weatherPoints.value = result
        }
    }




    private fun getArgentineCapitals(): List<Pair<String, Pair<Double, Double>>> {
        return listOf(
            "Buenos Aires (CABA)" to Pair(-34.60, -58.37),
            "La Plata" to Pair(-34.92, -57.95),
            "Catamarca" to Pair(-28.47, -65.78),
            "Chaco" to Pair(-27.45, -58.98),
            "Chubut" to Pair(-43.30, -65.02),
            "Córdoba" to Pair(-31.42, -64.18),
            "Corrientes" to Pair(-27.48, -58.83),
            "Entre Ríos" to Pair(-31.73, -60.53),
            "Formosa" to Pair(-26.18, -58.17),
            "Jujuy" to Pair(-24.18, -65.30),
            "La Pampa" to Pair(-36.62, -64.28),
            "La Rioja" to Pair(-29.42, -66.85),
            "Mendoza" to Pair(-32.89, -68.84),
            "Misiones" to Pair(-27.37, -55.88),
            "Neuquén" to Pair(-38.95, -68.06),
            "Río Negro" to Pair(-41.13, -63.00),
            "Salta" to Pair(-24.78, -65.40),
            "San Juan" to Pair(-31.53, -68.52),
            "San Luis" to Pair(-33.32, -66.33),
            "Santa Cruz" to Pair(-51.62, -69.22),
            "Santa Fe" to Pair(-31.65, -60.70),
            "Santiago del Estero" to Pair(-27.78, -64.27),
            "Tierra del Fuego" to Pair(-54.80, -68.30),
            "Tucumán" to Pair(-26.82, -65.22)
        )
    }


}
