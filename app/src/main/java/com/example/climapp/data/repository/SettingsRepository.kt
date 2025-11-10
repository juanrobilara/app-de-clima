package com.example.climapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class TempUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F")
}

enum class ThemeSetting(val value: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark")
}

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object Keys {
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val THEME = stringPreferencesKey("theme_setting")
    }

    val tempUnit: Flow<TempUnit> = context.dataStore.data.map { preferences ->
        val unitName = preferences[Keys.TEMP_UNIT] ?: TempUnit.CELSIUS.name
        TempUnit.valueOf(unitName)
    }

    suspend fun setTempUnit(unit: TempUnit) {
        context.dataStore.edit { preferences ->
            preferences[Keys.TEMP_UNIT] = unit.name
        }
    }

    val themeSetting: Flow<ThemeSetting> = context.dataStore.data.map { preferences ->
        val themeName = preferences[Keys.THEME] ?: ThemeSetting.SYSTEM.name
        ThemeSetting.valueOf(themeName)
    }

    suspend fun setThemeSetting(theme: ThemeSetting) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME] = theme.name
        }
    }
}