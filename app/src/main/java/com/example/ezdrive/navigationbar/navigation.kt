package com.example.ezdrive.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ezdrive.homescreen.CarRentalHomeScreen
import com.example.ezdrive.loginpage.LoginScreen
import com.example.ezdrive.loginpage.RegisterScreen
import com.example.ezdrive.navigationbar.BottomNavigationPane
import com.example.ezdrive.navigationbar.NavItem
import com.example.ezdrive.profile.ProfileScreen
import com.example.ezdrive.service.SessionManager
import com.example.ezdrive.service.handleLogin
import com.example.ezdrive.service.handleRegister
import com.example.ezdrive.screens.SewaScreen

@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val isLoggedIn = sessionManager.isLoggedIn()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: if (isLoggedIn) NavItem.Home.route else "login"

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
                BottomNavigationPane(
                    currentRoute = currentRoute,
                    onItemSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(NavItem.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) NavItem.Home.route else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    onLogin = { email, password ->
                        handleLogin(context, email, password) { success, role ->
                            if (success) {
                                sessionManager.saveLogin(email, role)
                                Toast.makeText(context, "Login as $role", Toast.LENGTH_SHORT).show()
                                navController.navigate(NavItem.Home.route) {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegister = { name, email, password ->
                        handleRegister(context, name, email, password) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            composable(NavItem.Home.route) {
                CarRentalHomeScreen(
                    onNavigate = { navController.navigate(it) },
                    onCarClicked = { /* TODO */ },
                    onProfile = {
                        navController.navigate(NavItem.Profile.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavItem.Sewa.route) {
                SewaScreen(
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it) },
                    onBookingClick = { /* TODO */ }
                )
            }

            composable(NavItem.Profile.route) {
                ProfileScreen(
                    userEmail = sessionManager.getUserEmail() ?: "-",
                    userRole = sessionManager.getUserRole() ?: "-",
                    onLogout = {
                        sessionManager.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.navigate(NavItem.Home.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
