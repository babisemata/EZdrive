package com.example.ezdrive.homescreen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Car
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCarListScreen(
    onAddCarClicked: () -> Unit,
    onEditCarClicked: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dbHelper = remember { DBHelper(context) }

    var carList by remember { mutableStateOf<List<Car>>(emptyList()) }
    var refreshTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(refreshTrigger) {
        carList = dbHelper.getAllCars()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kelola Mobil") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCarClicked) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Mobil Baru")
            }
        }
    ) { padding ->

        // --- INI BAGIAN LOGIKA PENGECEKANNYA ---
        if (carList.isEmpty()) {
            // Tampilan jika tidak ada mobil di database
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada mobil.\nTekan tombol '+' untuk menambah.",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Tampilkan daftar mobil jika ada
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(carList) { car ->
                    ListItem(
                        headlineContent = { Text("${car.merk} ${car.model}") },
                        supportingContent = { Text("Rp ${car.hargaPerHari}/hari") },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { onEditCarClicked(car.carid) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Mobil")
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        val success = dbHelper.deleteCar(car.carid)
                                        launch(Dispatchers.Main) {
                                            if (success) {
                                                Toast.makeText(context, "Mobil dihapus", Toast.LENGTH_SHORT).show()
                                                refreshTrigger = !refreshTrigger
                                            } else {
                                                Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus Mobil", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}