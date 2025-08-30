package com.golash.app.ui.screens.gallery

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.golash.app.R
import com.golash.app.data.model.Product
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.WarmSand

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit = {},
    minCellWidth: Dp = 150.dp
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val columns = (screenWidth / minCellWidth).toInt().coerceAtLeast(1)
    val cellWidth = screenWidth / columns

    val aspectRatio = 1280f / 960f

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Linen)
    ) {
        when (val state = uiState) {

            is GalleryUIState.Loading -> {}
            is GalleryUIState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

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
                        )  {
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
                                    modifier = Modifier.size(width = containerWidth, height = containerHeight),
                                    colors = CardDefaults.cardColors(containerColor = WarmSand)) {

                                        if (product.primaryImage?.type?.name == stringResource(R.string.resource)) {
                                            val resourceId =
                                                product.primaryImage?.url?.toIntOrNull()
                                            resourceId?.let { id ->
                                                Image(
                                                    painterResource(id = id),
                                                    contentDescription = stringResource(R.string.product_image),
                                                    modifier = Modifier
                                                        .fillMaxSize(),
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

            is GalleryUIState.Error -> {}
        }
    }
}