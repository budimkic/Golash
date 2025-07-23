package com.golash.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.golash.app.data.model.Product
import com.golash.app.ui.theme.Linen

@Composable
fun DetailScreen(product: Product) {


    Column(modifier = Modifier.fillMaxSize().background(Linen)) {
        AsyncImage(model = product.imageUrls)
    }

}
