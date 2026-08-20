package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.gateway.GatewayScreen
import com.example.ui.main.MainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Die StartDestination ist zwingend und unveränderlich IMMER der "gateway" ('Tritt ein') Screen.
    // Bei jedem App-Start oder Neustart wird stets zuerst dieser Gateway-Screen geladen.
    NavHost(
        navController = navController,
        startDestination = "gateway"
    ) {
        composable(
            route = "gateway",
            exitTransition = {
                fadeOut(animationSpec = tween(400))
            }
        ) {
            GatewayScreen(
                onEnterClick = {
                    navController.navigate("main") {
                        popUpTo("gateway") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "main",
            enterTransition = {
                fadeIn(animationSpec = tween(400))
            }
        ) {
            MainScreen()
        }
    }
}
