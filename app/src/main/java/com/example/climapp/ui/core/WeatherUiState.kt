package com.example.climapp.ui.core

import com.example.climapp.domain.model.WeatherResponse

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: WeatherResponse) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}