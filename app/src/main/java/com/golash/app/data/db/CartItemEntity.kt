package com.golash.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val price: Double,
    val productDescription: String,

    //ProductDetails
    /*----------------------------*/
    val size: String,
    val careInstructions: String,
    val materials: String,
    /*----------------------------*/

    val images: List<ProductImageEntity>,
    val quantity: Int
)

data class ProductImageEntity(
    val url: String,
    val type: String
)