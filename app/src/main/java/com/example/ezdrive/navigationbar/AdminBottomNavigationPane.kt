package com.example.ezdrive.navigationbar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.ezdrive.navigationbar.AdminNavItem

@Composable
fun AdminBottomNavigationPane(
    currentRoute: String?,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        AdminNavItem.Dashboard,
        AdminNavItem.ManageCars,
        AdminNavItem.Profile
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item.route) },
                // Gunakan imageVector = item.icon, bukan painterResource
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}