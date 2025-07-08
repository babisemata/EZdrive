package com.example.ezdrive.navigationbar


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : NavItem("home", Icons.Filled.Home, "Home")
    object Sewa : NavItem("sewa", Icons.Filled.CarRental, "Sewa")
    object Profile : NavItem("profile", Icons.Filled.People, "Profile")
}
