package com.golash.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductResponse(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "shortDescription") val shortDescription: String,
    @field:Json(name = "details") val details: ProductDetailsResponse,
    @field:Json(name = "price") val price: Double,
    @field:Json(name = "images") val images: List<ProductImageResponse>
)

@JsonClass(generateAdapter = true)
data class ProductDetailsResponse(
    @field:Json(name = "size") val size: String,
    @field:Json(name = "careInstructions") val careInstructions: String,
    @field:Json(name = "materials") val materials: String
)

@JsonClass(generateAdapter = true)
data class ProductImageResponse(
    @field:Json(name = "url") val url: String,
    @field:Json(name = "type") val type: String
)
