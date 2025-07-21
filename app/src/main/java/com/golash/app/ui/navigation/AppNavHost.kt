package com.golash.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.golash.app.ui.screens.cart.CartScreen
import com.golash.app.ui.screens.detail.DetailScreen
import com.golash.app.ui.screens.gallery.GalleryScreen
import com.golash.app.ui.screens.home.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: Destination, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(Destination.HOME.route) {
            HomeScreen(navController)
        }
        composable(
            Destination.GALLERY.route
        ) {
            GalleryScreen()
        }
        composable(Destination.CART.route) {
            CartScreen()
        }
        composable(Destination.PRODUCT_DETAIL.route) {
            DetailScreen()
        }
    }
}