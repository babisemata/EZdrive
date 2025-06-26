package com.example.ezdrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ezdrive.homescreen.CarRentalHomeScreen
import com.example.ezdrive.theme.EZDriveTheme
import com.example.ezdrive.loginpage.LoginScreen
import com.example.ezdrive.loginpage.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. State untuk tracking screen mana yang tampil
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
            // 2. State login (jika perlu)
            var isLoggedIn by remember { mutableStateOf(false) }

            EZDriveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        Screen.Login -> {
                            LoginScreen(
                                onLogin = { email, password ->
                                    // TODO: autentikasi, kalau sukses:
                                    isLoggedIn = true
                                    currentScreen = Screen.Home
                                },
                                onNavigateToRegister = {
                                    currentScreen = Screen.Register
                                }
                            )
                        }
                        Screen.Register -> {
                            RegisterScreen(
                                onRegister = { name, email, password ->
                                    // TODO: kirim data registrasi ke backend
                                    // lalu kembali ke login (atau langsung login):
                                    currentScreen = Screen.Login
                                },
                                onNavigateToLogin = {
                                    currentScreen = Screen.Login
                                }
                            )
                        }
                        Screen.Home -> {
                            CarRentalHomeScreen { carItem ->
                                println("Mobil diklik: ${carItem.name}")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Enum untuk memudahkan switch screen
enum class Screen { Login, Register, Home }


// Anda dapat memindahkan preview ke file terpisah atau di bawah Composable utama Anda
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    EZDriveTheme { // Ganti EZDriveTheme dengan nama tema aplikasi Anda
        CarRentalHomeScreen(onCarClicked = {})
    }
}