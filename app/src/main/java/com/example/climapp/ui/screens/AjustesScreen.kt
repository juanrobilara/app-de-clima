package com.example.climapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.climapp.data.repository.TempUnit
import com.example.climapp.data.repository.ThemeSetting
import com.example.climapp.ui.components.MainScaffold
import com.example.climapp.ui.viewmodel.SettingsViewModel

@Composable
fun AjustesScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val tempUnit by viewModel.tempUnit.collectAsState()
    val themeSetting by viewModel.themeSetting.collectAsState()

    MainScaffold(
        "Ajustes", "ajustes",
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("home") { inclusive = false }; launchSingleTop = true
            }
        },
        showFab = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Ajustes", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(24.dp))

            SettingRow(
                icon = Icons.Default.Thermostat,
                title = "Unidad de Temperatura"
            ) {
                val tempOptions = listOf(TempUnit.CELSIUS.symbol, TempUnit.FAHRENHEIT.symbol)
                val currentTempIndex = if (tempUnit == TempUnit.CELSIUS) 0 else 1

                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    tempOptions.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = tempOptions.size),
                            onClick = {
                                val newUnit = if (index == 0) TempUnit.CELSIUS else TempUnit.FAHRENHEIT
                                viewModel.setTempUnit(newUnit)
                            },
                            selected = index == currentTempIndex
                        ) {
                            Text(label)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            SettingRow(
                icon = Icons.Default.BrightnessAuto,
                title = "Tema de la aplicación"
            ) {
                val themeOptions = listOf(
                    ThemeSetting.SYSTEM to Icons.Default.BrightnessAuto,
                    ThemeSetting.LIGHT to Icons.Default.LightMode,
                    ThemeSetting.DARK to Icons.Default.Nightlight
                )
                val currentThemeIndex = themeOptions.indexOfFirst { it.first == themeSetting }

                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    themeOptions.forEachIndexed { index, (theme, icon) ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size),
                            onClick = { viewModel.setThemeSetting(theme) },
                            selected = index == currentThemeIndex,
                            icon = { SegmentedButtonDefaults.Icon(active = index == currentThemeIndex, { Icon(icon, null) }) }
                        ) {
                            Text(theme.name.capitalizeFirst())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

private fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}