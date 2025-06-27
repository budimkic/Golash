package com.golash.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.golash.app.ui.screens.detail.DetailScreen
import com.golash.app.ui.screens.home.HomeScreen

@Composable
fun GolashNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(onProductClick = { productId -> navController.navigate("detail/$productId") })
        }
        composable(
            "detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            productId?.let { DetailScreen(productId = it) }

        }
    }
}