package com.golash.app.data.repository


import android.util.Log
import com.google.firebase.firestore.firestore
import com.golash.app.domain.model.Product
import com.golash.app.domain.model.ProductDetails
import com.golash.app.domain.model.ProductImage
import com.golash.app.domain.repository.ProductRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class FirestoreProductRepository @Inject constructor() : ProductRepository {

    private val db = Firebase.firestore

    override suspend fun getProducts(): List<Product> {
        return try {
            val result = db.collection("products").get().await()
            Log.d("FirestoreDebug", "Successfully fetched ${result.size()} documents")
            result.documents.map { document ->
                val detailsMap = document.get("details") as? Map<*, *>
                val productDetails = ProductDetails(
                    sizes = (detailsMap?.get("sizes") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                    careInstructions = detailsMap?.get("careInstructions") as? String ?: "",
                    materials = detailsMap?.get("materials") as? String ?: ""
                )

                val imagesList = document.get("images") as? List<*>
                val productImages = imagesList?.mapNotNull { imageObj ->
                    val imageMap = imageObj as? Map<*, *>
                    val url = imageMap?.get("url") as? String ?: ""
                    val typeStr = imageMap?.get("type") as? String ?: "REMOTE"
                    val imageType = try {
                        ProductImage.ImageType.valueOf(typeStr)
                    } catch (e: Exception) {
                        ProductImage.ImageType.REMOTE
                    }
                    ProductImage(url = url, type = imageType)
                } ?: emptyList()

                Product(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    shortDescription = document.getString("shortDescription") ?: "",
                    details = productDetails,
                    price = document.getDouble("price") ?: 0.0,
                    images = productImages
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("FirestoreDebug", "Error fetching products", e)
            emptyList()
        }
    }

    override suspend fun getProductById(productId: String): Product? {
        return try {
            val document = db.collection("products").document(productId).get().await()
            if (!document.exists()) return null

            val detailsMap = document.get("details") as? Map<*, *>
            val productDetails = ProductDetails(
                sizes = (detailsMap?.get("sizes") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                careInstructions = detailsMap?.get("careInstructions") as? String ?: "",
                materials = detailsMap?.get("materials") as? String ?: ""
            )

            val imagesList = document.get("images") as? List<*>
            val productImages = imagesList?.mapNotNull { imageObj ->
                val imageMap = imageObj as? Map<*, *>
                val url = imageMap?.get("url") as? String ?: ""
                val typeStr = imageMap?.get("type") as? String ?: "REMOTE"
                val imageType = try {
                    ProductImage.ImageType.valueOf(typeStr)
                } catch (e: Exception) {
                    ProductImage.ImageType.REMOTE
                }
                ProductImage(url = url, type = imageType)
            } ?: emptyList()

            Product(
                id = document.id,
                name = document.getString("name") ?: "",
                shortDescription = document.getString("shortDescription") ?: "",
                details = productDetails,
                price = document.getDouble("price") ?: 0.0,
                images = productImages
            )
        } catch (e: Exception) {
            null
        }
    }
}