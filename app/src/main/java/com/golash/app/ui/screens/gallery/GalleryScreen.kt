package com.golash.app.ui.screens.gallery

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.golash.app.R
import com.golash.app.ui.components.OakTreeLoader
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.Ivory
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.WarmSand
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.sin



@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(), navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GalleryContent(
        uiState,
        onProductClick = { productId -> navController.navigate("product_detail/$productId") })
}

@Composable
private fun GalleryContent(
    uiState: GalleryState, onProductClick: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Linen)
    ) {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(1000, easing = EaseInOut)) togetherWith
                        fadeOut(animationSpec = tween(400, easing = EaseInOut))
            },
            label = "gallery_crossfade"
        ) { state ->
            when (state) {
                is GalleryState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Linen),
                        contentAlignment = Alignment.Center
                    ) {
                        OakTreeLoader(modifier = Modifier.wrapContentSize())
                    }
                }
                is GalleryState.Success -> {
                    GalleryProductGrid(state, onProductClick)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun GalleryProductGrid(state: GalleryState, onProductClick: (String) -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val minCellWidth: Dp = 150.dp
    val columns = (screenWidth / minCellWidth).toInt().coerceAtLeast(1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val state = state as GalleryState.Success

        items(state.products.size) { index ->
            val product = state.products[index]

            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(12.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .clickable { onProductClick(product.id) },
                colors = CardDefaults.cardColors(containerColor = WarmSand),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val containerWidth = 110.dp
                    val aspectRatio = 1280f / 960f
                    val containerHeight = containerWidth * aspectRatio
                    Card(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier.size(
                            width = containerWidth, height = containerHeight
                        ),
                        colors = CardDefaults.cardColors(containerColor = WarmSand)
                    ) {

                        if (product.primaryImage?.type?.name == stringResource(R.string.resource)) {
                            val resourceId = product.primaryImage?.url?.toIntOrNull()
                            resourceId?.let { id ->
                                Image(
                                    painterResource(id = id),
                                    contentDescription = stringResource(R.string.product_image),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } ?: Log.e(
                                "RotatingProductCard",
                                stringResource(R.string.error_invalid_resource)
                            )
                        } else if (product.primaryImage?.type?.name == stringResource(
                                R.string.remote
                            )
                        ) {
                            AsyncImage(
                                model = product.primaryImage?.url,
                                contentDescription = stringResource(R.string.product_image),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.name,
                        fontFamily = Marcellus,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = EarthBrown
                    )

                }
            }
        }
    }
}
