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
            val result = db.collection(COLLECTION_PRODUCTS).get().await()
            Log.d(TAG, "Successfully fetched ${result.size()} documents")
            result.documents.map { document ->
                val detailsMap = document.get(FIELD_DETAILS) as? Map<*, *>
                val productDetails = ProductDetails(
                    sizes = (detailsMap?.get(FIELD_SIZES) as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                    careInstructions = detailsMap?.get(FIELD_CARE_INSTRUCTIONS) as? String ?: "",
                    materials = detailsMap?.get(FIELD_MATERIALS) as? String ?: ""
                )

                val imagesList = document.get(FIELD_IMAGES) as? List<*>
                val productImages = imagesList?.mapNotNull { imageObj ->
                    val imageMap = imageObj as? Map<*, *>
                    val url = imageMap?.get(FIELD_URL) as? String ?: ""
                    val typeStr = imageMap?.get(FIELD_TYPE) as? String ?: DEFAULT_IMAGE_TYPE
                    val imageType = try {
                        ProductImage.ImageType.valueOf(typeStr)
                    } catch (e: Exception) {
                        ProductImage.ImageType.REMOTE
                    }
                    ProductImage(url = url, type = imageType)
                } ?: emptyList()

                Product(
                    id = document.id,
                    name = document.getString(FIELD_NAME) ?: "",
                    shortDescription = document.getString(FIELD_SHORT_DESCRIPTION) ?: "",
                    details = productDetails,
                    price = document.getDouble(FIELD_PRICE) ?: 0.0,
                    images = productImages
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products", e)
            emptyList()
        }
    }

    override suspend fun getProductById(productId: String): Product? {
        return try {
            val document = db.collection(COLLECTION_PRODUCTS).document(productId).get().await()
            if (!document.exists()) return null

            val detailsMap = document.get(FIELD_DETAILS) as? Map<*, *>
            val productDetails = ProductDetails(
                sizes = (detailsMap?.get(FIELD_SIZES) as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                careInstructions = detailsMap?.get(FIELD_CARE_INSTRUCTIONS) as? String ?: "",
                materials = detailsMap?.get(FIELD_MATERIALS) as? String ?: ""
            )

            val imagesList = document.get(FIELD_IMAGES) as? List<*>
            val productImages = imagesList?.mapNotNull { imageObj ->
                val imageMap = imageObj as? Map<*, *>
                val url = imageMap?.get(FIELD_URL) as? String ?: ""
                val typeStr = imageMap?.get(FIELD_TYPE) as? String ?: DEFAULT_IMAGE_TYPE
                val imageType = try {
                    ProductImage.ImageType.valueOf(typeStr)
                } catch (e: Exception) {
                    ProductImage.ImageType.REMOTE
                }
                ProductImage(url = url, type = imageType)
            } ?: emptyList()

            Product(
                id = document.id,
                name = document.getString(FIELD_NAME) ?: "",
                shortDescription = document.getString(FIELD_SHORT_DESCRIPTION) ?: "",
                details = productDetails,
                price = document.getDouble(FIELD_PRICE) ?: 0.0,
                images = productImages
            )
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val TAG = "FirestoreDebug"

        // Firestore Collections & Fields
        private const val COLLECTION_PRODUCTS = "products"
        private const val FIELD_DETAILS = "details"
        private const val FIELD_SIZES = "sizes"
        private const val FIELD_CARE_INSTRUCTIONS = "careInstructions"
        private const val FIELD_MATERIALS = "materials"
        private const val FIELD_IMAGES = "images"
        private const val FIELD_URL = "url"
        private const val FIELD_TYPE = "type"
        private const val FIELD_NAME = "name"
        private const val FIELD_SHORT_DESCRIPTION = "shortDescription"
        private const val FIELD_PRICE = "price"
        private const val DEFAULT_IMAGE_TYPE = "REMOTE"
    }
}