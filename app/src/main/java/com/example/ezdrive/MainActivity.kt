package com.example.ezdrive // Ganti dengan package aplikasi Anda

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
import com.example.ezdrive.screen.CarRentalHomeScreen
import com.example.ezdrive.theme.EZDriveTheme
import com.example.ezdrive.loginpage.LoginScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. remember / mutableStateOf harus di dalam sini
            var isLoggedIn by remember { mutableStateOf(false) }

            // 2. Semua UI Composable juga di dalam sini
            EZDriveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoggedIn) {
                        LoginScreen { email, password ->
                            // TODO: autentikasi
                            isLoggedIn = true
                        }
                    } else {
                        CarRentalHomeScreen { carItem ->
                            println("Mobil diklik: ${carItem.name}")
                        }
                    }
                }
            }
        }
    }
}

// Anda dapat memindahkan preview ke file terpisah atau di bawah Composable utama Anda
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    EZDriveTheme { // Ganti EZDriveTheme dengan nama tema aplikasi Anda
        CarRentalHomeScreen(onCarClicked = {})
    }
}