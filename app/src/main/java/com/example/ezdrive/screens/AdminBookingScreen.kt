package com.example.ezdrive.screens


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Booking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingScreen() {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val scope = rememberCoroutineScope()
    var bookingList by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var refreshTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(refreshTrigger) {
        bookingList = dbHelper.getAllBookings()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kelola Booking") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookingList) { booking ->
                AdminBookingCard(
                    booking = booking,
                    onStatusChange = { newStatus ->
                        scope.launch {
                            val success = dbHelper.updateBookingStatus(booking.bookingId, newStatus)
                            launch(Dispatchers.Main) {
                                if (success) {
                                    Toast.makeText(context, "Status diubah", Toast.LENGTH_SHORT).show()
                                    refreshTrigger = !refreshTrigger // Picu refresh
                                } else {
                                    Toast.makeText(context, "Gagal mengubah status", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingCard(
    booking: Booking,
    onStatusChange: (String) -> Unit
) {
    val statusOptions = listOf("Confirmed", "Paid", "Completed", "Cancelled")
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(booking.status) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(booking.carName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("User ID: ${booking.userId}")
            Text("Tanggal: ${booking.startDate} s/d ${booking.endDate}")

            // Dropdown untuk mengubah status
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    statusOptions.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                selectedStatus = status
                                expanded = false
                                onStatusChange(status) // Panggil callback dengan status baru
                            }
                        )
                    }
                }
            }
        }
    }
}