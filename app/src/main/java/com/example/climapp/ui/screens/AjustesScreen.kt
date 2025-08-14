package com.example.climapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.climapp.ui.components.MainScaffold

@Composable
fun AjustesScreen (navController: NavHostController) {

    MainScaffold("Ajustes" ,"ajustes",
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("home") { inclusive = false }; launchSingleTop = true
            }
        },
        showFab = false

    ) {

        Column {
            Text("Ajustes")
        }

    }
}