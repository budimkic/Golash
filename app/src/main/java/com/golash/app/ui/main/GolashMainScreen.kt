package com.golash.app.ui.main

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
import com.golash.app.ui.navigation.AppNavHost
import com.golash.app.ui.navigation.Destination
import com.golash.app.ui.theme.DarkChestnut
import com.golash.app.ui.theme.Marcellus
import com.golash.app.ui.theme.Oak
import com.golash.app.ui.theme.WarmSand


@Composable
fun GolashMainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = modifier, bottomBar = {
            NavigationBar(
                containerColor = WarmSand, windowInsets = NavigationBarDefaults.windowInsets
            ) {
                Destination.bottomNavDestinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route) {
                                launchSingleTop = true
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                            }
                            selectedDestination = index
                        },
                        icon = {
                            destination.icon?.let { icon ->
                                Icon(icon, contentDescription = destination.contentDescription)
                            }
                        },
                        label = {
                            destination.label?.let { label ->
                                Text(label, fontFamily = Marcellus, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkChestnut,
                            unselectedIconColor = Oak,
                            selectedTextColor = DarkChestnut,
                            unselectedTextColor = Oak,
                            indicatorColor = Color(0x99CBBF9D)
                        ),
                    )
                }
            }
        })


    { contentPadding ->
        AppNavHost(navController, Destination.HOME, modifier = Modifier.padding(contentPadding))
    }
}