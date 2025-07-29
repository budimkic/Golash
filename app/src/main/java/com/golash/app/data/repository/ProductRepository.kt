package com.golash.app.data.repository

import com.golash.app.data.model.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor() {

    val image1 = "https://www.culturevulturedirect.co.uk/images/products/large/65124.jpg"
    val image2 =
        "https://cdn.shoplightspeed.com/shops/665096/files/48099076/1500x4000x3/crosstree-lane-blue-canvas-boho-bag.jpg"
    val image3 =
        "https://earthhero.com/cdn/shop/files/gladrags-white-reusable-day-pad-reusable-period-pad-100-cotton-handmade-1-pad-w-2-inserts-30650895401071.jpg?v=1719325921"

    val listOfImages = listOf(image1, image2, image3)

    private val products = listOf(
        Product(
            "1",
            "Haremke",
            "Boho pantalone u harem fazonu, najlepse pantalone, " +
                    "divne pantalone, aman odusevice vas sloboda nosenja, " +
                    "dole SNS, ziveo Hrist! Zivot je lep.",
            5500.0,
            "https://www.culturevulturedirect.co.uk/images/products/large/65124.jpg"
        ),
        Product(
            "2",
            "Boho Bag",
            "Torba za stvari",
            4500.0,
            "https://cdn.shoplightspeed.com/shops/665096/files/48099076/1500x4000x3/crosstree-lane-blue-canvas-boho-bag.jpg"
        ),
        Product(
            "3",
            "Mesecarka",
            "Ulozak za vas",
            2500.0,
            "https://earthhero.com/cdn/shop/files/gladrags-white-reusable-day-pad-reusable-period-pad-100-cotton-handmade-1-pad-w-2-inserts-30650895401071.jpg?v=1719325921"
        ),
        Product("4", "Novcanik", "Cuvajte pare", 3500.0, listOfImages[0], listOfImages)
    )

    fun getProducts(): List<Product> = products
    fun getProductById(productId: String): Product? {
        return products.find { it.id == productId }
    }
}