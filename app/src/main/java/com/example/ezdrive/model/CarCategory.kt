package com.example.ezdrive.model

import androidx.compose.ui.graphics.vector.ImageVector

data class CarCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val availableCarCount: Int = 0 // Opsional, jika Anda ingin menampilkan jumlah
)