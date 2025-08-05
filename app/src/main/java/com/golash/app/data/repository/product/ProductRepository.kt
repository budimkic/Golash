package com.golash.app.data.repository.product

import com.golash.app.data.model.Product

interface ProductRepository{
    fun getProducts(): List<Product>
    fun getProductById(productId: String): Product?
}