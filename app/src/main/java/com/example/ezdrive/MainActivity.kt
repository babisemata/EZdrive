package com.example.ezdrive

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.ezdrive.homescreen.CarRentalHomeScreen
import com.example.ezdrive.loginpage.LoginScreen
import com.example.ezdrive.loginpage.RegisterScreen
import com.example.ezdrive.navigationbar.NavItem
import com.example.ezdrive.profile.ProfileScreen
import com.example.ezdrive.service.SessionManager
import com.example.ezdrive.service.handleLogin
import com.example.ezdrive.service.handleRegister
import com.example.ezdrive.theme.EZDriveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EZDriveApp()
        }
    }
}

@Composable
fun EZDriveApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val navController = rememberNavController()

    var currentScreen by remember {
        mutableStateOf(
            if (sessionManager.isLoggedIn()) Screen.Home else Screen.Login
        )
    }

    EZDriveTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.Login -> {
                    LoginScreen(
                        onLogin = { email, password ->
                            handleLogin(context, email, password) { success, role ->
                                if (success) {
                                    sessionManager.saveLogin(email, role)
                                    Toast.makeText(context, "Login as $role", Toast.LENGTH_SHORT).show()
                                    currentScreen = Screen.Home
                                } else {
                                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onNavigateToRegister = {
                            currentScreen = Screen.Register
                        }
                    )
                }

                Screen.Register -> {
                    RegisterScreen(
                        onRegister = { name, email, password ->
                            handleRegister(context, name, email, password) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    currentScreen = Screen.Login
                                }
                            }
                        },
                        onNavigateToLogin = {
                            currentScreen = Screen.Login
                        }
                    )
                }

                Screen.Home -> {
                    CarRentalHomeScreen(
                        onCarClicked = { carItem ->
                            println("Mobil diklik: ${carItem.name}")
                        },
                        onProfile = {
                            currentScreen = Screen.Profile
                        },
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        currentRoute = NavItem.Home.route,
                    )
                }

                Screen.Profile -> {
                    ProfileScreen(
                        userEmail = sessionManager.getUserEmail() ?: "-",
                        userRole = sessionManager.getUserRole() ?: "-",
                        onLogout = {
                            sessionManager.clearSession()
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                            currentScreen = Screen.Login
                        },
                        onBack = {
                            currentScreen = Screen.Home
                        }
                    )
                }
            }
        }
    }
}


enum class Screen {
    Login, Register, Home, Profile
}


@Preview(showBackground = true)
@Composable
fun PreviewCarRentalHome() {
    EZDriveTheme {
        CarRentalHomeScreen(onCarClicked = {}, onProfile = {}, onNavigate = {}, currentRoute = NavItem.Home.route)
    }
}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    EZDriveTheme {
//        CarRentalHomeScreen(onCarClicked = {})
//    }
//}
