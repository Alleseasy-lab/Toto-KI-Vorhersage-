package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.gateway.GatewayScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "gateway") {
        composable("gateway") {
            GatewayScreen(
                onEnterClick = {
                    navController.navigate("main") {
                        popUpTo("gateway") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            // Main app screen with bottom navigation will go here
            // Placeholder for now
            com.example.ui.main.MainScreen()
        }
    }
}
