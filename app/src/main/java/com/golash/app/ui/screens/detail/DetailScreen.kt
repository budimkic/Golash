package com.golash.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.golash.app.data.model.Product
import com.golash.app.ui.theme.Linen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.golash.app.ui.theme.Clay
import com.golash.app.ui.theme.DarkChestnut
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.Inter
import com.golash.app.ui.theme.Ivory
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.PurpleGrey80
import com.golash.app.ui.theme.WarmSand
import kotlin.math.absoluteValue

private const val MIN_ALPHA = 0.5f
private const val MAX_ALPHA = 1f
private const val PAGER_ASPECT_RATIO = 1f


@Composable
fun DetailScreen(viewModel: DetailViewModel = hiltViewModel()) {
    //val uiState = viewModel.uiState.value
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is DetailUiState.Success -> {
            DetailContent(product = state.product)
        }

        is DetailUiState.Loading -> {}
        is DetailUiState.Error -> {}
    }
}

@Composable
fun DetailContent(product: Product) {
    val pagerState = rememberPagerState(pageCount = { product.imageUrls.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Linen)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PAGER_ASPECT_RATIO)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = true,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                PagerImageItem(product, pagerState, page)
            }
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val activeColor = Ivory
                val inactiveColor = Ivory.copy(0.5f)
                if (pagerState.pageCount > 1) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) activeColor else inactiveColor

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(DeepBark.copy(alpha = 0.4f))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(6.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = product.name,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
            fontSize = 24.sp,
            color = DeepBark,
            fontFamily = Marcellus
        )

        Text(
            text = product.description,
            modifier = Modifier.padding(vertical = 4.dp).align(Alignment.CenterHorizontally),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal, color = DeepBark, fontFamily = Marcellus, lineHeight = 24.sp
        )

        Text(
            text = product.price.toString().plus(" RSD"),
            modifier = Modifier.padding(vertical = 4.dp).align(Alignment.CenterHorizontally),
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal, color = DeepBark, fontFamily = Marcellus
        )


    }
}

@Composable
private fun PagerImageItem(product: Product, pagerState: PagerState, page: Int) {
    val calculateAlphaFraction: (Float) -> Float = { 1f - it.coerceIn(0f, 1f) }
    Card(
        Modifier
            .fillMaxWidth()
            .graphicsLayer {
                val pageOffset = ((pagerState.currentPage - page)
                        + pagerState.currentPageOffsetFraction).absoluteValue
                alpha = lerp(
                    start = MIN_ALPHA, stop = MAX_ALPHA,
                    fraction = calculateAlphaFraction(pageOffset)
                )
            }, shape = RectangleShape
    ) {
        AsyncImage(
            model = product.imageUrls[page],
            contentDescription = "",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
