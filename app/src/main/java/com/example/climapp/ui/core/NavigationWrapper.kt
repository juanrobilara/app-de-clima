package com.example.climapp.ui.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.climapp.ui.screens.AjustesScreen
import com.example.climapp.ui.screens.FavScreen
import com.example.climapp.ui.screens.HomeScreen
import com.example.climapp.ui.screens.WeatherMapScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {

        composable("home") { HomeScreen(navController = navController) }
        composable("favoritos") {FavScreen(navController = navController)  }
        composable("mapa") {WeatherMapScreen(navController = navController) }
        composable("ajustes") {AjustesScreen(navController = navController) }

    }

}