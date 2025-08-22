package com.golash.app

import androidx.compose.runtime.Composable
import com.golash.app.ui.main.GolashMainScreen
import com.golash.app.ui.theme.GolashTheme

@Composable
fun GolashApp() {
    GolashTheme {
        GolashMainScreen()
    }
}

