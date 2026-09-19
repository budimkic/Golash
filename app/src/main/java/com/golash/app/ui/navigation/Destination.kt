package com.golash.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.golash.app.R

sealed class Destination(
    open val route: String,
    open val icon: ImageVector? = null,
    @get:StringRes open val contentDescription: Int? = null,
    @get:StringRes open val label: Int? = null,
    open val productId: String? = null
) {
    data object HOME : Destination(
        "home", Icons.Filled.Home, R.string.go_to_home_screen, R.string.home
    )

    data object GALLERY : Destination(
        "gallery",
        Icons.Filled.GridView,
        R.string.visit_the_gallery,
        R.string.gallery
    )

    data object CART : Destination(
        "cart",
        Icons.Filled.ShoppingCart,
        R.string.check_your_cart,
            R.string.cart
    )

    data object PRODUCT_DETAIL : Destination(
        "product_detail/{productId}", productId = "productId"
    )

    data object CHECKOUT : Destination(
        route = "checkout"
    )

    companion object {
        val bottomNavDestinations: List<Destination> = listOf(HOME, GALLERY, CART)
    }
}