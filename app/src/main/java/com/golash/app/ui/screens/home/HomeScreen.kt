package com.golash.app.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golash.app.R
import com.golash.app.data.repository.FakeProductRepository
import com.golash.app.ui.components.RotatingProductCard
import com.golash.app.ui.theme.CormorantGaramondItalic
import com.golash.app.ui.theme.DarkGray
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.RawCotton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(navController: NavController, onProductClick: (String) -> Unit = {}) {
    val repository = FakeProductRepository()
    val products = repository.getProducts()
    val scrollState = rememberScrollState()

    var showText by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(false) }

    var hasShownAnimation by rememberSaveable { mutableStateOf(false) }

    var selectedProductId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!hasShownAnimation) {
            showText = true
            delay(2000)
            showCard = true
            delay(500)
            hasShownAnimation = true
        } else {
            showText = true
            showCard = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Linen)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showText,
            enter = if (!hasShownAnimation) fadeIn(animationSpec = tween(3300)) else fadeIn(tween(0))
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_golash),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(90.dp),
                contentDescription = ""
            )
        }

        AnimatedVisibility(
            visible = showText,
            enter = if (!hasShownAnimation) fadeIn(animationSpec = tween(3300)) else fadeIn(tween(0))
        ) {
            Text(
                text = "Beri bosiljak pred zalazak, tada je najmirisniji.",
                fontFamily = CormorantGaramondItalic,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBark,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp, bottom = 50.dp)
            )
        }

        /*  AnimatedVisibility(
              visible = showText,
              enter = if (!hasShownAnimation) fadeIn(animationSpec = tween(3300)) else fadeIn(tween(0))
          ) {
              Image(
                  painter = painterResource(id = R.drawable.oak_icon_mod),
                  modifier = Modifier.size(60.dp),
                  contentDescription = ""
              )
          }*/


        AnimatedVisibility(
            visible = showCard,
            enter = if (!hasShownAnimation) fadeIn(animationSpec = tween(3300)) else fadeIn(tween(0))
        ) {
            RotatingProductCard(
                products = products,
                onProductClick = { id ->
                    navController.navigate("product_detail")
                },
                fadeInEnabled = showCard && !hasShownAnimation,
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}


