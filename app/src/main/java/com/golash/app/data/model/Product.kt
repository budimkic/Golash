package com.golash.app.data.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrls: List<String>
) {
    val imageUrl: String get() = imageUrls.firstOrNull() ?: ""
}