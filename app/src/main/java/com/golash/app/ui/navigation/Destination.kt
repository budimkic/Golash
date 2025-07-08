package com.golash.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val icon: ImageVector,
    val contentDescription: String,
    val label: String
) {
    HOME("home", Icons.Filled.Home, "Go to Home Screen", "Home"),
    GALLERY("gallery", Icons.Filled.GridView, "Visit the Gallery", "Gallery"),
    CART("cart", Icons.Filled.ShoppingCart, "Check your Cart", "Cart")
}