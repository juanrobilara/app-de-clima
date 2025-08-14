package com.example.climapp.ui.screens

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.climapp.ui.viewmodel.WeatherViewModel
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.weathericons.WeatherIcons
import com.mikepenz.iconics.utils.sizeDp
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weatherState by viewModel.currentWeather.collectAsStateWithLifecycle()
    val suggestions by viewModel.citySuggestions.collectAsState()
    val history by viewModel.getHistory().collectAsState(initial = emptyList())

    var query by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<CityEntity?>(null) }


    MainScaffold("Home",
        "home",
        onNavigate = { route ->
        navController.navigate(route) {
            popUpTo("home") { inclusive = false }; launchSingleTop = true
        }
    },
        showFab = true
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Spacer(Modifier.size(48.dp))


            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    selectedCity = null
                    showSuggestions = it.isNotBlank()
                    viewModel.clearSuggestions()
                    viewModel.updateSearchQuery(it)
                },
                label = { Text("Buscar ciudad") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        selectedCity?.let {
                            viewModel.fetchCurrentWeather(it.lat.toString(), it.lon.toString())
                            showSuggestions = false
                        }
                    }
                ),
                modifier = Modifier
                    .padding(top = 60.dp)
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp))
            )

            DropdownMenu(
                expanded = showSuggestions && suggestions.isNotEmpty(),
                onDismissRequest = { showSuggestions = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                suggestions.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city.name) },
                        onClick = {
                            selectedCity = CityEntity(0, city.name, city.latitude, city.longitude)
                            query = city.name
                            showSuggestions = false
                            viewModel.fetchCurrentWeather(city.latitude.toString(), city.longitude.toString())
                        }
                    )
                }
            }

            if (history.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("🕓 Historial:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                history.forEach { city ->
                    Text(
                        text = city.name,
                        modifier = Modifier
                            .clickable {
                                selectedCity = city
                                query = city.name
                                viewModel.fetchCurrentWeather(city.lat.toString(), city.lon.toString())
                            }
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                weatherState?.let { WeatherDisplay(it) } ?: CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    selectedCity?.let {
                        viewModel.fetchCurrentWeather(it.lat.toString(), it.lon.toString())
                    }
                },
                enabled = selectedCity != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Actualizar Clima")
            }
        }

    }

}


@Composable
fun WeatherDisplay(weatherState: WeatherResponse?) {
    weatherState?.let { weatherResponse ->
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
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                WeatherIcon(icon = getWeatherIcon(condition))
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "$temp°C - $condition",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "$humidity% de Humedad",
                        color = textColor
                    )
                    Text(
                        text = "Viento a $wind km/h dirección $direction",
                        color = textColor
                    )
                }
            }
        }
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
        key.contains("despejado") -> listOf(Color.White, Color.Yellow.copy(0.7f))
        key.contains("nublado") -> listOf(Color.White, Color.LightGray.copy(0.7f))
        key.contains("lluvia") -> listOf(Color.Gray, Color.Blue.copy(0.7f))
        key.contains("llovizna") -> listOf(Color.LightGray, Color.Cyan.copy(0.6f))
        key.contains("nieve") -> listOf(Color.White, Color(0xFFADD8E6))
        key.contains("niebla") -> listOf(Color.Gray, Color.LightGray.copy(0.6f))
        key.contains("niebla densa") -> listOf(Color.DarkGray, Color.Gray.copy(0.6f))
        key.contains("neblina") -> listOf(Color.LightGray, Color.White.copy(0.6f))
        key.contains("humo") -> listOf(Color.DarkGray, Color.LightGray.copy(0.5f))
        key.contains("polvo") -> listOf(Color(0xFFA1887F), Color(0xFFD7CCC8)) // marrón suave
        key.contains("tormenta de arena") -> listOf(Color(0xFFBCAAA4), Color(0xFFE0B69C))
        key.contains("ceniza") -> listOf(Color.Gray, Color.DarkGray.copy(0.7f))
        key.contains("ráfagas") -> listOf(Color.Cyan, Color.LightGray.copy(0.6f))
        key.contains("tornado") -> listOf(Color.Black, Color.DarkGray.copy(0.8f))
        key.contains("tormenta") -> listOf(Color.DarkGray, Color.Blue.copy(0.7f))
        key.contains("ventoso") -> listOf(Color.White, Color.LightGray.copy(0.7f))
        else -> listOf(Color.Green, Color.White.copy(0.7f))
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
        key.contains("ceniza") -> Color.Yellow
        key.contains("ráfagas") -> Color.Black
        key.contains("tornado") -> Color.Yellow
        key.contains("tormenta") -> Color.Yellow
        key.contains("ventoso") -> Color.Black
        else -> Color.White
    }
}


@Composable
fun WeatherIcon(icon: WeatherIcons.Icon) {
    val context = LocalContext.current

    AndroidView(
        factory = { ImageView(context) },
        update = { imageView ->
            val drawable = IconicsDrawable(context, icon).apply {
                sizeDp = 64
            }
            imageView.setImageDrawable(drawable)
        },
        modifier = Modifier.size(64.dp)
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