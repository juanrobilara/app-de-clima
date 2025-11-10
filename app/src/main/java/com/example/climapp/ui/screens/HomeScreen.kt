package com.example.climapp.ui.screens

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.widget.ImageView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.climapp.R
import com.example.climapp.data.local.CityEntity
import com.example.climapp.domain.model.WeatherResponse
import com.example.climapp.domain.model.toCelsius
import com.example.climapp.domain.model.translateCondition
import com.example.climapp.domain.model.windDirectionFromDegrees
import com.example.climapp.domain.model.windSpeedToKilometers
import com.example.climapp.ui.components.MainScaffold
import com.example.climapp.ui.core.WeatherUiState
import com.example.climapp.ui.viewmodel.WeatherViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.weathericons.WeatherIcons
import com.mikepenz.iconics.utils.sizeDp
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()
    val weatherMap by viewModel.cityWeatherMap.collectAsStateWithLifecycle()
    var cityToManage by remember { mutableStateOf<CityEntity?>(null) }

    MainScaffold(
        "Mis Ciudades",
        "home",
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("home") { inclusive = false }; launchSingleTop = true
            }
        },
        showFab = true
    ) { paddingValues ->

        cityToManage?.let { city ->
            ManageCityDialog(
                city = city,
                onDismiss = { cityToManage = null },
                onEdit = {
                    //proximamente impl editar
                    cityToManage = null
                },
                onDelete = {
                    viewModel.deleteCity(city)
                    cityToManage = null
                }
            )
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (savedCities.isEmpty()) {
                item {
                    Text(
                        "No hay ciudades guardadas. Toca el botón '+' para agregar una.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }

            items(savedCities, key = { it.id }) { city ->
                val weatherState = weatherMap[city.id]
                CityWeatherCard(
                    city = city,
                    weatherState = weatherState,
                    onRefresh = { viewModel.refreshWeatherForCity(city) },
                    onLongClick = {
                        cityToManage = city
                    }
                )
            }
        }
    }
}

// ahora manejo las cards de las distintas ciudades
@Composable
fun ManageCityDialog(
    city: CityEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = city.name)
        },
        text = {
            Text("¿Deseas eliminar esta ciudad?")
        },
        confirmButton = {
            TextButton(
                onClick = onDelete
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onEdit
            ) {
                Text("Cancelar", color = Color.Red)
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityWeatherCard(
    city: CityEntity,
    weatherState: WeatherUiState?,
    onRefresh: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .combinedClickable(
                onClick = { },
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Crossfade(targetState = weatherState, label = "WeatherStateAnimation") { state ->
            when (state) {
                is WeatherUiState.Success -> {
                    WeatherDisplay(weatherResponse = state.weather)
                }
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is WeatherUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Error al cargar ${city.name}", color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = onRefresh) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
@Composable
fun WeatherDisplay(weatherResponse: WeatherResponse) {


    val temp = weatherResponse.main.temp.toCelsius().roundToInt()
    val condition = translateCondition(weatherResponse.weather[0].main)
    val humidity = weatherResponse.main.humidity
    val wind = windSpeedToKilometers(weatherResponse.wind.speed).roundToInt()
    val direction = windDirectionFromDegrees(weatherResponse.wind.deg)

    val brush = Brush.linearGradient(colors = getBackgroundColor(condition))
    val textColor = getTextColor(condition)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {

                    Text(
                        text = weatherResponse.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        lineHeight = 40.sp
                    )


                    Text(
                        text = "$temp°C",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        lineHeight = 72.sp
                    )
                }


                WeatherIcon(
                    icon = getWeatherIcon(condition),
                    modifier = Modifier
                        .size(96.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(Modifier.height(16.dp))


            Text(
                text = condition,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                WeatherDetailItem(
                    icon = Icons.Outlined.WaterDrop,
                    label = "Humedad",
                    value = "$humidity%",
                    textColor = textColor
                )


                WeatherDetailItem(
                    icon = Icons.Outlined.Air,
                    label = "Viento",
                    value = "$wind km/h $direction",
                    textColor = textColor
                )
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    textColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}




fun getWeatherIcon(keyWeather: String): WeatherIcons.Icon {
    val weatherIcon =
        when {
            keyWeather.contains("Despejado") -> WeatherIcons.Icon.wic_day_sunny
            keyWeather.contains("Nublado") -> WeatherIcons.Icon.wic_day_cloudy
            keyWeather.contains("Lluvia") -> WeatherIcons.Icon.wic_day_rain
            keyWeather.contains("Nieve") -> WeatherIcons.Icon.wic_day_snow
            keyWeather.contains("Tormenta") -> WeatherIcons.Icon.wic_day_storm_showers
            keyWeather.contains("Ventoso") -> WeatherIcons.Icon.wic_day_windy
            else ->
                WeatherIcons.Icon.wic_alien
        }
    return weatherIcon
}

fun getBackgroundColor(keyWeather: String): List<Color> {
    val key = keyWeather.lowercase()

    return when {
        key.contains("despejado") -> listOf(Color(0xFF81C7F5), Color(0xFF39A0ED))
        key.contains("nublado") -> listOf(Color(0xFFB0BEC5), Color(0xFF78909C))
        key.contains("lluvia") -> listOf(Color(0xFF546E7A), Color(0xFF29434E))
        key.contains("llovizna") -> listOf(Color(0xFF90A4AE), Color(0xFF607D8B))
        key.contains("nieve") -> listOf(Color(0xFFFAFAFA), Color(0xFFE0E0E0))
        key.contains("niebla") -> listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E))
        key.contains("niebla densa") -> listOf(Color(0xFF757575), Color(0xFF424242))
        key.contains("neblina") -> listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
        key.contains("humo") -> listOf(Color(0xFF757575), Color(0xFF424242))
        key.contains("polvo") -> listOf(Color(0xFFA1887F), Color(0xFF795548))
        key.contains("tormenta de arena") -> listOf(Color(0xFFD2B48C), Color(0xFFB8860B))
        key.contains("ceniza") -> listOf(Color(0xFF616161), Color(0xFF424242))
        key.contains("ráfagas") -> listOf(Color(0xFF4FC3F7), Color(0xFF039BE5))
        key.contains("tornado") -> listOf(Color(0xFF424242), Color(0xFF212121))
        key.contains("tormenta") -> listOf(Color(0xFF37474F), Color(0xFF263238))
        key.contains("ventoso") -> listOf(Color(0xFFB3E5FC), Color(0xFF81D4FA))
        else -> listOf(Color(0xFFB0BEC5), Color(0xFF78909C))
    }
}

fun getTextColor(keyWeather: String): Color {
    val key = keyWeather.lowercase()

    return when {
        key.contains("despejado") -> Color.Black
        key.contains("nublado") -> Color.Black
        key.contains("lluvia") -> Color.White
        key.contains("llovizna") -> Color.Black
        key.contains("nieve") -> Color.Black
        key.contains("niebla") -> Color.Black
        key.contains("niebla densa") -> Color.White
        key.contains("neblina") -> Color.Black
        key.contains("humo") -> Color.White
        key.contains("polvo") -> Color.Black
        key.contains("tormenta de arena") -> Color.Black
        key.contains("ceniza") -> Color.White
        key.contains("ráfagas") -> Color.Black
        key.contains("tornado") -> Color.White
        key.contains("tormenta") -> Color.White
        key.contains("ventoso") -> Color.Black
        else -> Color.Black
    }
}


@Composable
fun WeatherIcon(icon: WeatherIcons.Icon, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        factory = { ImageView(context) },
        update = { imageView ->
            val drawable = IconicsDrawable(context, icon).apply {
                sizeDp = 96
            }
            imageView.setImageDrawable(drawable)
        },
        modifier = modifier
    )
}

@Composable
fun CustomText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    textAlign: TextAlign? = null,
    color: Color = Color.Black,
    maxLines: Int = Int.MAX_VALUE,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontFamily = FontFamily(Font(R.font.applegaramond)),
        textAlign = textAlign,
        color = color,
        maxLines = maxLines,
        lineHeight = lineHeight,
    )
}