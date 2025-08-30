package com.golash.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.golash.app.data.model.ProductDetails
import com.golash.app.data.model.ProductImage

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val price: Double,
    val productDescription: String,
    val details: ProductDetails,
    val images: List<ProductImage>,
    val quantity: Int
)