package com.example.ezdrive.theme // Ganti dengan package aplikasi Anda

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Definisikan palet warna Anda (Anda bisa membuatnya lebih detail di Color.kt)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Contoh warna ungu
    secondary = Color(0xFF03DAC6), // Contoh warna teal
    tertiary = Color(0xFF03A9F4),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    primaryContainer = Color(0xFFBB86FC), // Contoh warna untuk TopAppBar
    onPrimaryContainer = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC5),
    tertiary = Color(0xFF039BE5),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE1E1E1),
    onSurface = Color(0xFFE1E1E1),
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color.White
)

@Composable
fun EZDriveTheme( // Ganti nama tema ini jika perlu
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Dynamic color di Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asumsikan Anda memiliki Typography yang didefinisikan di Type.kt
        content = content
    )
}