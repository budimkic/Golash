package com.golash.app.data.repository

import com.golash.app.data.model.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor() {
    fun getProducts(): List<Product> = listOf(
        Product("1", "Haremke", "Boho pantalone u harem fazonu", 5500.0, "https://www.culturevulturedirect.co.uk/images/products/large/65124.jpg"),
        Product("2", "Boho Bag", "Torba za stvari", 4500.0, "https://cdn.shoplightspeed.com/shops/665096/files/48099076/1500x4000x3/crosstree-lane-blue-canvas-boho-bag.jpg"),
        Product("3", "Mesecarka", "Ulozak za vas", 2500.0, "https://earthhero.com/cdn/shop/files/gladrags-white-reusable-day-pad-reusable-period-pad-100-cotton-handmade-1-pad-w-2-inserts-30650895401071.jpg?v=1719325921"),
        Product("3", "Novcanik", "Cuvajte pare", 3500.0, "https://earthhero.com/cdn/shop/files/gladrags-white-reusable-day-pad-reusable-period-pad-100-cotton-handmade-1-pad-w-2-inserts-30650895401071.jpg?v=1719325921")
    )
}