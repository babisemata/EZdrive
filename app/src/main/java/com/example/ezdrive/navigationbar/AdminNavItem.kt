package com.example.ezdrive.navigationbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

// Ganti tipe data icon dari Int menjadi ImageVector
sealed class AdminNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : AdminNavItem(
        "admin_dashboard",
        "Dashboard",
        Icons.Filled.Dashboard
    )
    object ManageCars : AdminNavItem(
        "admin_manage_cars",
        "Mobil",
        Icons.Filled.DirectionsCar
    )
    object Profile : AdminNavItem(
        "admin_profile",
        "Profil",
        Icons.Filled.Person
    )
}