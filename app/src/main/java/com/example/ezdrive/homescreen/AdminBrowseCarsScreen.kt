package com.example.ezdrive.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ezdrive.model.Car
import androidx.compose.ui.platform.LocalContext
import com.example.ezdrive.helper.DBHelper
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminBrowseCarsScreen(
    dbHelper: DBHelper,
    onCarClicked: (Int) -> Unit
) {
    // definisikan sekali saja
    val formatRupiah = remember {
        NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    }
    val categories = remember { dbHelper.getAllCategories() }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val cars = remember(selectedCategory) {
        if (selectedCategory == null || selectedCategory == "all") {
            dbHelper.getAllCars()
        } else {
            dbHelper.getAllCars().filter { it.category == selectedCategory }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(cars) { car ->
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onCarClicked(car.carid) }
            ) {
                Column {
                    AsyncImage(
                        model = car.foto,
                        contentDescription = "${car.merk} ${car.model}",
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${car.merk} ${car.model}",
                        modifier = Modifier.padding(start = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = formatRupiah.format(car.hargaPerHari ?: 0.0),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}
