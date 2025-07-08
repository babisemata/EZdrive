package com.example.ezdrive.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezdrive.homescreen.CarItem
import com.example.ezdrive.R

// Dummy data sementara, bisa diganti dengan repository / API
private val allCars = listOf(
    CarItem(1, "Toyota Avanza", "MPV", "Rp 350.000", R.drawable.img_car_avanza, 4.5f, 7, "Manual"),
    CarItem(2, "Honda HR-V", "SUV", "Rp 550.000", R.drawable.img_car_hrv, 4.8f, 5, "Automatic"),
    CarItem(3, "Suzuki Ertiga", "MPV", "Rp 300.000", R.drawable.img_car_ertiga, 4.3f, 7, "Manual"),
    CarItem(4, "Mitsubishi Xpander", "MPV", "Rp 400.000", R.drawable.img_car_xpander, 4.6f, 7, "Automatic"),
    CarItem(5, "Daihatsu Terios", "SUV", "Rp 450.000", R.drawable.img_car_terios, 4.4f, 7, "Manual"),
    CarItem(6, "Honda Brio", "Hatchback", "Rp 250.000", R.drawable.img_car_brio, 4.2f, 5, "Automatic")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onCarClicked: (CarItem) -> Unit
) {
    var query by remember { mutableStateOf(TextFieldValue("")) }

    val filteredCars = remember(query.text) {
        if (query.text.isBlank()) allCars
        else allCars.filter {
            it.name.contains(query.text, ignoreCase = true) ||
                    it.type.contains(query.text, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Cari mobil...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCars) { car ->
                CarSearchResultItem(car = car, onClick = { onCarClicked(car) })
            }
        }
    }
}

@Composable
fun CarSearchResultItem(car: CarItem, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(car.imageUrl),
                contentDescription = car.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(car.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(car.type, color = Color.Gray, fontSize = 14.sp)
                Text(car.pricePerDay, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
