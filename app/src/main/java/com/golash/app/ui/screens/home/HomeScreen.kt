package com.golash.app.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.golash.app.data.repository.FakeProductRepository
import com.golash.app.ui.components.ProductCard


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(onProductClick = {})
}

@Composable
fun HomeScreen(onProductClick: (String) -> Unit = {}) {
    val repository = FakeProductRepository()
    val products = repository.getProducts()

    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(products) { product ->
            ProductCard(product = product, onClick = {
                onProductClick(product.id)
            })
        }
    }
}