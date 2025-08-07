package com.golash.app.ui.screens.cart

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.golash.app.data.model.Cart
import com.golash.app.data.model.CartItem
import com.golash.app.data.model.Product
import com.golash.app.ui.theme.Linen

@Composable
fun CartScreen(cartViewModel: CartViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {cartViewModel.refresh()}
    val cartState by cartViewModel.cartState.collectAsState()

    when (cartState) {
        is CartState.Loading -> {Log.d("Cart Loading", "")}
        is CartState.Success -> {
            Log.d("Cart Success", "")
            val cart = (cartState as CartState.Success).cart
            CartContent(cart = cart, onRemove = {})
        }

        is CartState.Error -> {}
    }

}

@Composable
private fun CartContent(cart: Cart, onRemove: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Linen)
    ) {
        if (cart.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(cart.totalItems) { index ->
                    val cartItem = cart.items[index]
                    CartItemRow(
                        cartItem = cartItem,
                        onRemove = {})
                    HorizontalDivider()
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (cart.items.isNotEmpty()) {
            Row() {
                Text(
                    text =
                        "Total: ${"%.2f".format(cart.totalPrice)} RSD",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = cart.items.isNotEmpty()
        ) { Text("ORDER") }
    }
}

@Composable
private fun CartItemRow(cartItem: CartItem, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cartItem.product.imageUrl,
            contentDescription = cartItem.product.name, modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(cartItem.product.name, style = MaterialTheme.typography.titleMedium)
            Text("$${"%.2f".format(cartItem.product.price)}", color = Color.Gray)
            Text("Qty: ${cartItem.quantity}", color = Color.Gray)
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}

