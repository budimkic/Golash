package com.golash.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.golash.app.ui.navigation.AppNavHost
import com.golash.app.ui.navigation.Destination
import com.golash.app.ui.theme.Clay
import com.golash.app.ui.theme.DarkChestnut
import com.golash.app.ui.theme.DeepBark
import com.golash.app.ui.theme.DeepOlive
import com.golash.app.ui.theme.DriedWheat
import com.golash.app.ui.theme.EarthBrown
import com.golash.app.ui.theme.GolashTheme
import com.golash.app.ui.theme.Linen
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.RawCotton
import com.golash.app.ui.theme.WarmSand

@Composable
fun GolashApp() {
    GolashTheme {
        GolashMainScreen()
    }
}

@Composable
fun GolashMainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = WarmSand,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = {
                            Text(
                                destination.label,
                                fontFamily = Marcellus,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkChestnut,
                            unselectedIconColor = Oak,
                            selectedTextColor = DarkChestnut,
                            unselectedTextColor = Oak,
                            indicatorColor = Color(0x99CBBF9D) // ili DriedWheat ako je bar tamniji
                        ),
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController, startDestination, modifier = Modifier.padding(contentPadding))

    }
}
