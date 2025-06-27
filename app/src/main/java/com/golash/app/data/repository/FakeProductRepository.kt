package com.golash.app.data.repository

import com.golash.app.data.model.Product

class FakeProductRepository () {
    fun getProducts(): List<Product> = listOf(
        Product("1", "Lorem ipsum dolor sit ame", "Boho pantalone u harem fazonu", 5500.0, "url"),
        Product("2", "Boho Bag", "Torba za stvari", 4500.0, "url"),
        Product("3", "Mesecarka", "Ulozak za vas", 4500.0, "url"),
        Product("3", "Novcanik", "Cuvajte pare", 4500.0, "url")
    )
}