package com.example.ezdrive.detailscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.homescreen.CarItem
import com.example.ezdrive.homescreen.toCarItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    carId: Int,
    onBack: () -> Unit,
    onRentNow: (CarItem) -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    var carItem by remember { mutableStateOf<CarItem?>(null) }

    LaunchedEffect(carId) {
        val carFromDb = dbHelper.getCarById(carId)
        if (carFromDb != null) {
            carItem = carFromDb.toCarItem()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Mobil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (carItem != null) {
                val isCarAvailable = carItem!!.carFromDb.isAvailable
                Button(
                    enabled = isCarAvailable,
                    onClick = { onRentNow(carItem!!) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(if (isCarAvailable) "Sewa Sekarang" else "Telah Disewa")
                }
            }
        }
    ) { padding ->
        if (carItem == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = carItem!!.imageData,
                        contentDescription = carItem!!.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    )
                    if (!carItem!!.carFromDb.isAvailable) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak Tersedia",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(carItem!!.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                Text(carItem!!.type, color = Color.Gray)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${carItem!!.rating}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${carItem!!.seats} Kursi", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(carItem!!.transmission, fontSize = 14.sp)
                }

                Divider()

                Text("Harga Sewa", fontWeight = FontWeight.SemiBold)
                Text(
                    carItem!!.pricePerDay,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}