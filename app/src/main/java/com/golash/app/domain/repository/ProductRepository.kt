package com.golash.app.domain.repository

import com.golash.app.domain.model.Product

interface ProductRepository{
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(productId: String): Product?
}