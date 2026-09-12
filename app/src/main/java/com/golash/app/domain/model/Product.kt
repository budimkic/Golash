package com.golash.app.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    val id: String,
    val name: String,
    val shortDescription: String,
    val details: ProductDetails,
    val price: Double,
    val images: List<ProductImage>
) {
    val primaryImage: ProductImage? get() = images.firstOrNull()
}

@JsonClass(generateAdapter = true)
data class ProductDetails(
    val sizes: List<String>,
    val careInstructions: String,
    val materials: String
)

@JsonClass(generateAdapter = true)
data class ProductImage(
    val url: String,
    val type: ImageType = ImageType.REMOTE
) {
    enum class ImageType {
        REMOTE,
        LOCAL,
        RESOURCE
    }

    companion object {
        fun fromResource(resourceId: Int): ProductImage {
            return ProductImage(resourceId.toString(), ImageType.RESOURCE)
        }
    }
}