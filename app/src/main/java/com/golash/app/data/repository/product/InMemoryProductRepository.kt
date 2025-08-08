package com.golash.app.data.repository.product

import androidx.compose.ui.res.painterResource
import com.golash.app.R
import com.golash.app.data.model.Product
import com.golash.app.data.model.ProductImage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryProductRepository @Inject constructor() : ProductRepository {

    //Mock Data
    private val haremPants = R.drawable.harem_pants
    private val bag = R.drawable.torba2
    private val pad = R.drawable.pad
    private val wallet = R.drawable.wallet

    private val listOfImages = listOf(haremPants, bag, pad)

    private val products = listOf(
        Product(
            "1",
            "Nomad Pants",
            "Billowing, free-flowing linen trousers designed for movement, comfort, and effortless style. " + "Inspired by wandering souls and desert blooms, " + "they drape with grace and tell a story with every step.",
            5500.0,
            images = listOf(ProductImage.fromResource(haremPants))
        ), Product(
            "2",
            "Boho Bag",
            "Handcrafted with love from natural fibers, this Boho Bag combines earthy textures and vibrant patterns for a stylish yet practical companion. " + "Perfect for free spirits who carry their essentials " + "with a touch of artistic flair and eco-conscious soul.",
            4500.0,
            images = listOf(ProductImage.fromResource(bag))
        ), Product(
            "3",
            "Menstrual Pad",
            "Reusable menstrual pad made from soft, natural fabrics that provide comfort and reliable protection while being eco-friendly.",
            2500.0,
            images = listOf(ProductImage.fromResource(pad))
        ), Product(
            "4",
            "Wallet",
            "Handcrafted linen wallet with a slim, durable design, perfect for carrying cash, cards, " + "and small essentials while keeping an earthy, natural aesthetic.",
            3500.0,
            images = listOf(ProductImage.fromResource(wallet))
    ))

    override fun getProducts(): List<Product> = products
    override fun getProductById(productId: String): Product? {
        return products.find { it.id == productId }
    }
}