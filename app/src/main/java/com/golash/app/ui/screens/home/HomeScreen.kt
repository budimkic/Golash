package com.golash.app.ui.screens.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.golash.app.R
import com.golash.app.ui.components.OakTreeLoader
import com.golash.app.ui.components.RotatingProductCard
import com.golash.app.ui.theme.CormorantGaramondItalic
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.Linen
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Error) {
            val errorMessage = (uiState as HomeUiState.Error).message
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "Retry"
            )
                .let { result ->
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.refresh()
                    }
                }
        }
    }

    HomeContent(
        uiState = uiState,
        onProductClick = { productId ->
            navController.navigate("product_detail/$productId")
        }
    )
}
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onProductClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var isLocalLoading by remember { mutableStateOf(true) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Success) {
            delay(600)
            isLocalLoading = false
            delay(100)
            showContent = true
        } else {
            isLocalLoading = true
            showContent = false
        }
    }

    Crossfade(
        targetState = (uiState is HomeUiState.Loading || isLocalLoading),
        animationSpec = tween(700, easing = EaseInOut),
        label = "home_content_transition",
        modifier = Modifier.fillMaxSize().background(Linen)
    ) { currentlyLoading ->
        if (currentlyLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                OakTreeLoader(modifier = Modifier.wrapContentSize())
            }
        } else if (uiState is HomeUiState.Success) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val logoAlpha by animateFloatAsState(
                    targetValue = if (showContent) 1f else 0f,
                    animationSpec = tween(700)
                )
                Image(
                    painter = painterResource(id = R.drawable.logo_golash),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(90.dp)
                        .graphicsLayer(alpha = logoAlpha),
                    contentDescription = null
                )

                val textAlpha by animateFloatAsState(
                    targetValue = if (showContent) 1f else 0f,
                    animationSpec = tween(700)
                )
                Text(
                    text = "Plant a tree today, its shade will outlive you.",
                    fontFamily = CormorantGaramondItalic,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .graphicsLayer(alpha = textAlpha)
                )

                val cardAlpha by animateFloatAsState(
                    targetValue = if (showContent) 1f else 0f,
                    animationSpec = tween(1000, delayMillis = 400)
                )
                Box(
                    Modifier
                        .graphicsLayer(alpha = cardAlpha)
                        .padding(bottom = 60.dp)
                ) {
                    RotatingProductCard(
                        products = uiState.products,
                        onProductClick = onProductClick,
                        fadeInEnabled = false,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
    }
}