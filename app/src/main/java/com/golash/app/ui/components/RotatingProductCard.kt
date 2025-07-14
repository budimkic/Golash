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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.golash.app.R

import com.golash.app.data.model.Product
import com.golash.app.ui.theme.CormorantGaramond
import com.golash.app.ui.theme.CormorantGaramondItalic
import com.golash.app.ui.theme.DarkChestnut
import com.golash.app.ui.theme.DarkGray
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.RawCotton
import com.golash.app.ui.theme.WarmSand
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
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSand),
        elevation = CardDefaults.cardElevation(defaultElevation = 40.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
               // elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.size(290.dp)//.border(3.dp, DeepOlive, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            ) {
                AsyncImage(
                    model = products[currentIndex].imageUrl,
                    contentDescription = "My image",
                    modifier = Modifier.fillMaxSize()
                )
            }



            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${products[currentIndex].name}",
                fontFamily = Marcellus,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = EarthBrown

            )
            //  Spacer(modifier = Modifier.height(4.dp))
            /*    Text(
                    text = "${products[currentIndex].price} RSD",
                    fontFamily = Marcellus,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Linen

                )*/
        }
    }
}
