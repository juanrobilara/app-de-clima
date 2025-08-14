package com.example.climapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Icon
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.climapp.BuildConfig
import com.example.climapp.ui.components.MainScaffold
import com.example.climapp.ui.viewmodel.WeatherMapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.LocationServices
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import android.graphics.*
import android.util.Log

import androidx.compose.runtime.*

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import com.google.accompanist.permissions.rememberPermissionState
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.plugins.annotation.SymbolManager

import org.maplibre.android.plugins.annotation.SymbolOptions


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherMapScreen(
    navController: NavHostController,
    viewModel: WeatherMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val weatherPoints by viewModel.weatherPoints.collectAsState()
    val key = BuildConfig.MAP_KEY
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    val addedIcons = remember { mutableSetOf<String>() }
    val mapView = remember { MapView(context).apply { onCreate(null) } }

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleMap by remember { mutableStateOf<Style?>(null) }
    var symbolManager by remember { mutableStateOf<SymbolManager?>(null) }

    var userLocation by remember { mutableStateOf<Location?>(null) }

    val locationManager = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    // Pedir los permisos
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Obtener ubicación
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            val gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val net = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            userLocation = listOfNotNull(gps, net).maxByOrNull { it.time }
        }
    }

    // Manejo del CV
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    // Iniciar mapa
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize(),
        update = { mv ->
            mv.getMapAsync { map ->
                map.setStyle("https://api.maptiler.com/maps/streets/style.json?key=$key") { style ->
                    mapLibreMap = map
                    styleMap = style

                    // Centrar
                    userLocation?.let {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 10.0))
                        viewModel.fetchArgentineCapitalsWeather()
                    }


                    symbolManager = SymbolManager(mapView, map, style).apply {
                        iconAllowOverlap = true
                    }
                }
            }
        }
    )

    LaunchedEffect(weatherPoints) {
        val sm = symbolManager
        val style = styleMap

        if (sm != null && style != null) {
            sm.deleteAll()

            weatherPoints.forEach { point ->
                val latLng = LatLng(point.lat, point.lon)
                val iconId = "temp-${point.lat}-${point.lon}"
                val bitmap = createTemperatureBubbleBitmap(context, getColorForTemperature(point.temp), point.temp)

                if (!addedIcons.contains(iconId)) {
                    style.addImage(iconId, bitmap)
                    addedIcons.add(iconId)
                }

                val options = SymbolOptions()
                    .withLatLng(latLng)
                    .withIconImage(iconId)

                sm.create(options)
            }
        }
    }
}

fun getColorForTemperature(temp: Int): Int {
    return when {
        temp <= 0 -> android.graphics.Color.BLUE
        temp in 1..10 -> android.graphics.Color.CYAN
        temp in 11..20 -> android.graphics.Color.GREEN
        temp in 21..30 -> android.graphics.Color.YELLOW
        temp > 30 -> android.graphics.Color.RED
        else -> android.graphics.Color.GRAY
    }
}

fun createTemperatureBubbleBitmap(context: Context, color: Int, temp: Int): Bitmap {
    val size = 100
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint().apply {
        this.color = color
        isAntiAlias = true
    }

    canvas.drawCircle(size / 2f, size / 2f, size / 2.5f, paint)

    val textPaint = Paint().apply {
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    canvas.drawText("${temp}°", size / 2f, size / 1.8f, textPaint)

    return bitmap
}