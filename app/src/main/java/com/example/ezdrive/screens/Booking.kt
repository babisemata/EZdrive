package com.example.ezdrive.screens.booking

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Booking
import com.example.ezdrive.model.Car
import com.example.ezdrive.service.SessionManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private fun convertMillisToDateString(millis: Long, format: String): String {
    val formatter = SimpleDateFormat(format, Locale("id", "ID"))
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    carId: Int,
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var car by remember { mutableStateOf<Car?>(null) }
    var totalPrice by remember { mutableStateOf(0.0) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    var selectedStartDate by remember { mutableStateOf<Long?>(null) }
    var selectedEndDate by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(carId) {
        car = dbHelper.getCarById(carId)
    }

    LaunchedEffect(selectedStartDate, selectedEndDate, car) {
        val currentCar = car
        if (selectedStartDate != null && selectedEndDate != null && currentCar != null) {
            val days = TimeUnit.MILLISECONDS.toDays(selectedEndDate!! - selectedStartDate!!)
            if (days >= 0) {
                totalPrice = (days + 1) * (currentCar.hargaPerHari ?: 0.0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Mobil") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") } }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column {
                    Text("Total Harga", style = MaterialTheme.typography.bodyMedium)
                    Text("Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalPrice)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    // Tombol hanya aktif jika semua data sudah siap
                    enabled = selectedStartDate != null && selectedEndDate != null && car != null && selectedEndDate!! > selectedStartDate!!,
                    onClick = {
                        scope.launch {
                            val currentCar = car ?: return@launch // Keluar jika car null

                            sessionManager.getUserEmail()?.let { email ->
                                val userId = dbHelper.getUserIdByEmail(email)
                                if (userId != -1) {
                                    val newBooking = Booking(
                                        bookingId = 0,
                                        userId = userId,
                                        carId = currentCar.carid,
                                        carName = "${currentCar.merk ?: ""} ${currentCar.model ?: ""}",
                                        carImage = currentCar.foto ?: ByteArray(0),
                                        startDate = convertMillisToDateString(selectedStartDate!!, "yyyy-MM-dd"),
                                        endDate = convertMillisToDateString(selectedEndDate!!, "yyyy-MM-dd"),
                                        totalPrice = totalPrice,
                                        status = "Confirmed"
                                    )
                                    if (dbHelper.addBooking(newBooking)) {
                                        Toast.makeText(context, "Booking berhasil!", Toast.LENGTH_SHORT).show()
                                        onBookingSuccess()
                                    } else {
                                        Toast.makeText(context, "Gagal melakukan booking.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } ?: Toast.makeText(context, "Sesi tidak ditemukan.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Konfirmasi Booking")
                }
            }
        }
    ) { paddingValues ->
        val currentCar = car
        if (currentCar == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        AsyncImage(
                            model = currentCar.foto, contentDescription = currentCar.merk,
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(Modifier.padding(16.dp)) {
                            Text("${currentCar.merk} ${currentCar.model}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(currentCar.hargaPerHari)} / hari", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // --- Pemilihan Tanggal ---
                Text("Pilih Tanggal Sewa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // Tombol Tanggal Mulai
                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        selectedStartDate?.let { convertMillisToDateString(it, "dd MMMM yyyy") } ?: "Pilih Tanggal Mulai"
                    )
                }

                // Tombol Tanggal Selesai
                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        selectedEndDate?.let { convertMillisToDateString(it, "dd MMMM yyyy") } ?: "Pilih Tanggal Selesai"
                    )
                }
            }
        }
    }

    // --- Dialog Date Picker ---
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedStartDate = startDatePickerState.selectedDateMillis
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Batal") } }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedEndDate = endDatePickerState.selectedDateMillis
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Batal") } }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}