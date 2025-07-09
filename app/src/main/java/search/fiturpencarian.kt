package com.example.ezdrive.search

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.homescreen.CarItem
import com.example.ezdrive.homescreen.toCarItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fiturpencarian(
    onBack: () -> Unit,
    onCarClicked: (CarItem) -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }

    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<CarItem>>(emptyList()) }

    // LaunchedEffect untuk melakukan pencarian setiap kali query berubah
    LaunchedEffect(query) {
        // Beri jeda 300ms agar tidak mencari di setiap ketukan (debounce)
        delay(300)

        searchResults = if (query.isBlank()) {
            // Jika kosong, tampilkan semua mobil
            dbHelper.getAllCars().map { it.toCarItem() }
        } else {
            // Jika diisi, lakukan pencarian
            dbHelper.searchCars(query).map { it.toCarItem() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Cari mobil berdasarkan merk atau model...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(searchResults) { carItem ->
                CarSearchResultItem(car = carItem, onClick = { onCarClicked(carItem) })
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
            // Gunakan Coil untuk menampilkan gambar dari ByteArray
            AsyncImage(
                model = car.imageData,
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