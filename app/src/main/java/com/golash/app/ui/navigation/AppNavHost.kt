package com.golash.app.ui.navigation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.golash.app.ui.screens.cart.CartScreen
import com.golash.app.ui.screens.detail.DetailScreen
import com.golash.app.ui.screens.gallery.GalleryScreen
import com.golash.app.ui.screens.home.HomeScreen
import com.golash.app.ui.theme.Linen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier.background(Linen),
        enterTransition = { fadeIn(animationSpec = tween(100, easing = EaseInOut)) },
        exitTransition = { fadeOut(animationSpec = tween(100, easing = EaseInOut)) },
        popEnterTransition = { fadeIn(animationSpec = tween(100, easing = EaseInOut)) },
        popExitTransition = { fadeOut(animationSpec = tween(100, easing = EaseInOut)) }
    ) {
        composable(Destination.HOME.route) {
            HomeScreen(navController = navController)
        }
        composable(
            Destination.GALLERY.route
        ) {
            GalleryScreen(navController = navController)
        }
        composable(Destination.CART.route) {
            CartScreen()
        }
        composable(
            Destination.PRODUCT_DETAIL.route,
            arguments = listOf(navArgument("productId") {
                type = NavType.StringType
                nullable = false

            })
        ) {
            DetailScreen()
        }
    }
}