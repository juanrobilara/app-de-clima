package com.example.climapp.ui.screens

import android.widget.ImageView
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.climapp.R
import com.example.climapp.data.models.City
import com.example.climapp.data.models.cities
import com.example.climapp.domain.model.WeatherResponse
import com.example.climapp.domain.model.toCelsius
import com.example.climapp.domain.model.translateCondition
import com.example.climapp.ui.viewmodel.WeatherViewModel
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.weathericons.WeatherIcons
import com.mikepenz.iconics.utils.sizeDp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weatherState by viewModel.currentWeather.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf(cities[0]) }

Column (modifier
    .fillMaxSize()
    .padding(32.dp)
){


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            value = selectedCity.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Selecciona una ciudad") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city.name) },
                    onClick = {
                        selectedCity = city
                        expanded = false
                        viewModel.fetchCurrentWeather(city.lat, city.lon)
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    weatherState?.let { weather ->
        WeatherDisplay(weather)
    } ?: CircularProgressIndicator()

    Spacer(modifier = Modifier.size(16.dp))

    Button(onClick = {
        viewModel.fetchCurrentWeather(selectedCity.lat, selectedCity.lon)
    }) {
        Text("Actualizar Clima")
    }


}

}



@Composable
fun WeatherDisplay(
    weatherState: WeatherResponse?,
) {
    weatherState?.let { weatherResponse ->

        val temp = weatherResponse.main.temp.toCelsius().roundToInt()
        val condition = translateCondition(weatherResponse.weather[0].main)
        val humidity = weatherResponse.main.humidity
        val wind = weatherResponse.wind.speed

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(getBackgroundColor(condition),
                    shape = RoundedCornerShape(9.dp)
                )
                .border(
                    12.dp,
                    getBackgroundColor(condition),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Row (
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color.Green)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherIcon(icon = getWeatherIcon(keyWeather = condition))
                Spacer(modifier = Modifier.width(16.dp))
                Column (
                    modifier = Modifier
                        .fillMaxHeight()

                ) {
                    CustomText(
                        text = "$temp°C - $condition",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "$humidity% de Humedad",
                        color = Color.Black
                    )
                    Text(
                        text = "$wind mph viento",
                        color = Color.Black
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

fun getBackgroundColor(keyWeather: String): Color =
    when {
        keyWeather.contains("Despejado") -> Color.Cyan

        keyWeather.contains("Nublado") -> Color.DarkGray

        keyWeather.contains("Lluvia") -> Color.Yellow

        keyWeather.contains("Tormenta") -> Color.Red

        keyWeather.contains("Ventoso") -> Color.LightGray


        else -> Color.Green
    }


@Composable
fun WeatherIcon(icon: WeatherIcons.Icon) {
    val context = LocalContext.current
    val weatherIconDrawable =
        IconicsDrawable(context, icon).apply {
            sizeDp = 64
        }

    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                setImageDrawable(weatherIconDrawable)
            }
        },
        modifier = Modifier.size(64.dp),
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