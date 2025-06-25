package com.example.ezdrive // Ganti dengan package aplikasi Anda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ezdrive.screen.CarRentalHomeScreen
import com.example.ezdrive.theme.EZDriveTheme // Pastikan path tema Anda benar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Terapkan tema Material 3 Anda di sini
            // Biasanya ada di ui/theme/Theme.kt
            EZDriveTheme { // Ganti EZDriveTheme dengan nama tema aplikasi Anda
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Panggil Composable utama Anda di sini
                    CarRentalHomeScreen(onCarClicked = { carItem ->
                        // TODO: Implementasikan aksi ketika sebuah mobil diklik
                        // Misalnya, navigasi ke halaman detail mobil
                        println("Mobil diklik: ${carItem.name}")
                    })
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