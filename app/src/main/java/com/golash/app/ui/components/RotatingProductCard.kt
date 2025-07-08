package com.golash.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.golash.app.R

import com.golash.app.data.model.Product
import com.golash.app.ui.theme.DarkGray
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.RawCotton
import kotlinx.coroutines.delay


@Composable
fun RotatingProductCard(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    intervalMilis: Long = 5000L,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMilis)
            currentIndex = (currentIndex + 1) % products.size
        }
    }

    Card(
        modifier = modifier
            .wrapContentSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = RawCotton),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {




            Card(
                shape = RoundedCornerShape(25.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.size(220.dp)
            ) {
                AsyncImage(
                    model = products[currentIndex].imageUrl,
                    contentDescription = "My image",
                    modifier = Modifier.fillMaxSize()
                )
            }



            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${products[currentIndex].name}",
                fontFamily = Marcellus,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = DarkGray

            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${products[currentIndex].price} RSD",
                fontFamily = Marcellus,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = DarkGray

            )
        }
    }
}
