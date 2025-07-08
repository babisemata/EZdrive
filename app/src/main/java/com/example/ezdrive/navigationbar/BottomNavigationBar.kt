package com.example.ezdrive.navigationbar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavigationPane(
    currentRoute: String,
    onItemSelected: (route: String) -> Unit
) {
    NavigationBar {
        listOf(NavItem.Home, NavItem.Sewa, NavItem.Profile).forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item.route) }
            )
        }
    }
}
