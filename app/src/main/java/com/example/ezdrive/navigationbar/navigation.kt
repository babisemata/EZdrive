package com.example.ezdrive.navigationbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ezdrive.homescreen.CarRentalHomeScreen
import com.example.ezdrive.screens.sewa



sealed class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home     : NavItem("home",     Icons.Filled.Home,      "Home")
    object Bookings : NavItem("bookings", Icons.Filled.Bookmarks,"Bookings")
    object Profile  : NavItem("profile",  Icons.Filled.People,    "Profile")
}

private val navItems = listOf(
    NavItem.Home,
    NavItem.sewa,
    NavItem.Profile
)

@Composable
fun BottomNavigationPane(
    currentRoute: String,
    onItemSelected: (route: String) -> Unit
) {
    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item.route) },
                colors = NavigationBarItemDefaults.colors()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    // Buat NavController
    val navController = rememberNavController()

    // Dapatkan route aktif untuk highlight bottom bar
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: NavItem.Home.route

    Scaffold (
        bottomBar = {
            BottomNavigationPane(
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    navController.navigate(route) {
                        // Hindari penumpukan back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        // NavHost di dalam scaffold
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) {
                CarRentalHomeScreen (
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it) },
                    onCarClicked = { /* TODO: aksi klik mobil */ },
                    onProfile = { navController.navigate(NavItem.Profile.route) }
                )
            }
            composable(NavItem.Bookings.route) {
                sewa (
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it) },
                    onCarClicked = { /* TODO: aksi klik mobil */ },
                    onProfile = { navController.navigate(NavItem.Profile.route) }
                )
            }

            composable(NavItem.Profile.route) {
                ProfileScreen(
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it) }
                )
            }
        }
    }
}


