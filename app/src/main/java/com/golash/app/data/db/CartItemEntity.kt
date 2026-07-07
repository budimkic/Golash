package com.golash.app.data.db

import androidx.room.Entity

@Entity(tableName = "cart_items", primaryKeys = ["productId", "selectedSize"])
data class CartItemEntity(
    val productId: String,
    val name: String,
    val price: Double,
    val productDescription: String,

    //ProductDetails
    /*----------------------------*/
    val sizes: List<String>,
    val careInstructions: String,
    val materials: String,
    /*----------------------------*/

    val images: List<ProductImageEntity>,
    val selectedSize: String,
    val quantity: Int,

    val addedAt: Long
)

data class ProductImageEntity(
    val url: String,
    val type: String
)