package com.golash.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.golash.app.R
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
        },
        onRefresh = { viewModel.refresh() }
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState, onProductClick: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()
) {

    val scrollState = rememberScrollState()
    var initialAnimationState by rememberSaveable { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(false) }
    var showProducts by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!initialAnimationState) {
            showText = true
            delay(1000)
            showCard = true
            delay(200)
            initialAnimationState = true
            showProducts = true
        } else {
            showText = true
            showCard = true
            showProducts = true
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
            enter = if (!initialAnimationState)
                fadeIn(animationSpec = tween(2000))
            else
                fadeIn(tween(0))
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
            enter = if (!initialAnimationState)
                fadeIn(animationSpec = tween(3000))
            else fadeIn(tween(0))
        ) {
            Text(
                text = "Plant a tree today, " +
                        "its shade will outlive you.",
                fontFamily = CormorantGaramondItalic,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBark,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
            )
        }

        when (uiState) {
            is HomeUiState.Loading -> {
               /* ShimmerEffect(
                    isLoading = true,
                    contentAfterLoading = {}, modifier = Modifier.padding(32.dp),
                    height = 200.dp, shape = RoundedCornerShape(12.dp)
                ) */
            }

            is HomeUiState.Success -> {
                AnimatedVisibility(
                    visible = showCard,
                    enter = if (!initialAnimationState)
                        fadeIn(animationSpec = tween(1400))
                    else fadeIn(tween(0))
                ) {
                    RotatingProductCard(
                        products = uiState.products,
                        onProductClick = onProductClick,
                        fadeInEnabled = showCard && !initialAnimationState,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }

            is HomeUiState.Error -> {
                // Error state is handled by the Snackbar
            }
        }
    }
}



