package com.example.ezdrive.navigationbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ezdrive.homescreen.CarRentalHomeScreen
import com.example.ezdrive.profile.ProfileScreen
import com.example.ezdrive.screens.SewaScreen

// Nav item sealed class
sealed class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home    : NavItem("home",   Icons.Filled.Home,     "Home")
    object Sewa    : NavItem("sewa",   Icons.Filled.CarRental,"Sewa")
    object Profile : NavItem("profile",Icons.Filled.People,   "Profile")
}

// List item untuk bottom nav
private val navItems = listOf(
    NavItem.Home,
    NavItem.Sewa,
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
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: NavItem.Home.route

    Scaffold(
        bottomBar = {
            BottomNavigationPane(
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    navController.navigate(route) {
                        // ✅ ganti ini supaya tidak error: pakai string route saja
                        popUpTo(NavItem.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) {
                CarRentalHomeScreen(
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it) },
                    onCarClicked = {
                        // TODO: aksi ketika mobil diklik
                    },
                    onProfile = {
                        navController.navigate(NavItem.Profile.route) {
                            popUpTo(NavItem.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(NavItem.Sewa.route) {
                SewaScreen(
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it) },
                    onBookingClick = { booking ->
                        // TODO: aksi saat booking diklik
                    }
                )
            }
            composable(NavItem.Profile.route) {
                ProfileScreen(
                    userEmail = "user@example.com",      // ganti dengan data login user
                    userRole = "User",                   // ganti dengan role user
                    onLogout = {
                        // TODO: aksi logout
                    },
                    onBack = {
                        navController.navigate(NavItem.Home.route) {
                            popUpTo(NavItem.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
