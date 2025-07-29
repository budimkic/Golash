package com.golash.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import com.golash.app.ui.main.GolashMainScreen
import com.golash.app.ui.navigation.AppNavHost
import com.golash.app.ui.navigation.Destination
import com.golash.app.ui.theme.DarkChestnut
import com.golash.app.ui.theme.GolashTheme
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.WarmSand

@Composable
fun GolashApp() {
    GolashTheme {
        GolashMainScreen()
    }
}

