package com.example.ezdrive.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

fun mapIconNameToVector(iconName: String): ImageVector {
    return when (iconName) {
        "Filled.DirectionsCar" -> Icons.Filled.DirectionsCar
        "Filled.Speed" -> Icons.Filled.Speed
        "Filled.DriveEta" -> Icons.Filled.DriveEta
        "Filled.DirectionsBus" -> Icons.Filled.DirectionsBus
        "Filled.ElectricCar" -> Icons.Filled.ElectricCar
        else -> Icons.Filled.HelpOutline
    }
}