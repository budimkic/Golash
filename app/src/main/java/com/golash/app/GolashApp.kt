package com.golash.app

import androidx.compose.runtime.Composable
import com.golash.app.ui.navigation.GolashNavGraph
import com.golash.app.ui.theme.GolashTheme

@Composable
fun GolashApp() {
    GolashTheme {
        GolashNavGraph()
    }
}