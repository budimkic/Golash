package com.golash.app.data.db

import android.util.Log
import androidx.room.TypeConverter
import com.golash.app.data.model.ProductDetails
import com.golash.app.data.model.ProductImage
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @TypeConverter
    fun fromProductDetails(details: ProductDetails): String {
        val adapter = moshi.adapter(ProductDetails::class.java)
        return adapter.toJson(details)
    }

    @TypeConverter
    fun toProductDetails(json: String): ProductDetails? {
        if (json.isBlank()) return null
        return try {
            val adapter = moshi.adapter(ProductDetails::class.java)
            adapter.fromJson(json)
        } catch (e: Exception) {
            Log.e("Converters", "Error converting JSON to ProductDetails", e)
            null
        }
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String = list?.joinToString(",") ?: ""
    @TypeConverter
    fun toStringList(data: String): List<String> =
        if (data.isNullOrEmpty()) emptyList() else data.split(",")

    @TypeConverter
    fun fromProductImageList(images: List<ProductImage>): String {
        val listType = Types.newParameterizedType(List::class.java, ProductImage::class.java)
        val adapter = moshi.adapter<List<ProductImage>>(listType)
        return adapter.toJson(images)
    }
    @TypeConverter
    fun toProductImageList(json: String): List<ProductImage> {
        if (json.isEmpty()) return emptyList()
        return try {
            val listType = Types.newParameterizedType(List::class.java, ProductImage::class.java)
            val adapter = moshi.adapter<List<ProductImage>>(listType)
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}