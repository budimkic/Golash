package com.golash.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    open val route: String,
    open val icon: ImageVector? = null,
    open val contentDescription: String? = null,
    open val label: String? = null,
    open val productId: String? = null
) {
    data object HOME : Destination(
        "home", Icons.Filled.Home, "Go to Home Screen", "Home"
    )

    data object GALLERY : Destination(
        "gallery", Icons.Filled.GridView, "Visit the Gallery", "Gallery"
    )

    data object CART : Destination(
        "cart", Icons.Filled.ShoppingCart, "Check your Cart", "Cart"
    )

    data object PRODUCT_DETAIL : Destination(
        "product_detail/{productId}", productId = "productId"
    )

    companion object {
        val bottomNavDestinations: List<Destination> = listOf(HOME, GALLERY, CART)
    }


}