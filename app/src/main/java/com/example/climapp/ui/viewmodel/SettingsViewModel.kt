package com.example.climapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climapp.data.repository.SettingsRepository
import com.example.climapp.data.repository.TempUnit
import com.example.climapp.data.repository.ThemeSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val tempUnit = settingsRepository.tempUnit.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TempUnit.CELSIUS
    )

    val themeSetting = settingsRepository.themeSetting.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeSetting.SYSTEM
    )

    fun setTempUnit(unit: TempUnit) {
        viewModelScope.launch {
            settingsRepository.setTempUnit(unit)
        }
    }

    fun setThemeSetting(theme: ThemeSetting) {
        viewModelScope.launch {
            settingsRepository.setThemeSetting(theme)
        }
    }
}