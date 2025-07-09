package com.example.ezdrive.screens.booking

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Booking
import com.example.ezdrive.model.Car
import com.example.ezdrive.service.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// Helper function untuk konversi tanggal
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

    // State untuk data
    var car by remember { mutableStateOf<Car?>(null) }
    var totalPrice by remember { mutableStateOf(0.0) }

    // State untuk DatePicker
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    var selectedStartDate by remember { mutableStateOf<Long?>(null) }
    var selectedEndDate by remember { mutableStateOf<Long?>(null) }

    // Ambil data mobil saat layar dibuka
    LaunchedEffect(carId) {
        car = dbHelper.getCarById(carId)
    }

    // Hitung total harga setiap kali tanggal berubah
    LaunchedEffect(selectedStartDate, selectedEndDate) {
        if (selectedStartDate != null && selectedEndDate != null && car != null) {
            val days = TimeUnit.MILLISECONDS.toDays(selectedEndDate!! - selectedStartDate!!)
            if (days >= 0) {
                // Gunakan operator elvis (?:) untuk memberi nilai default
                totalPrice = (days + 1) * (car!!.hargaPerHari ?: 0.0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Mobil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column {
                    Text("Total Harga", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Rp ${"%,.0f".format(totalPrice)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    enabled = selectedStartDate != null && selectedEndDate != null && selectedEndDate!! > selectedStartDate!!,
                    onClick = {
                        scope.launch {
                            val userId = dbHelper.getUserIdByEmail(sessionManager.getUserEmail()!!)
                            if (userId != -1 && car != null) {
                                val newBooking = Booking(
                                    bookingId = 0,
                                    userId = userId,
                                    carId = car!!.carid,
                                    carName = "${car!!.merk} ${car!!.model}",
                                    // Gunakan operator elvis (?:) untuk memberi nilai default
                                    carImage = car!!.foto ?: ByteArray(0),
                                    startDate = convertMillisToDateString(selectedStartDate!!, "yyyy-MM-dd"),
                                    endDate = convertMillisToDateString(selectedEndDate!!, "yyyy-MM-dd"),
                                    totalPrice = totalPrice,
                                    status = "Confirmed"
                                )
                                if (dbHelper.addBooking(newBooking)) {
                                    Toast.makeText(context, "Booking berhasil!", Toast.LENGTH_SHORT).show()
                                    onBookingSuccess()
                                }
                            }
                        }
                    }
                ) {
                    Text("Konfirmasi Booking")
                }
            }
        }
    ) { paddingValues ->
        if (car == null) {
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
                // --- Detail Mobil ---
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        AsyncImage(
                            model = car!!.foto, contentDescription = car!!.merk,
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "${car!!.merk} ${car!!.model}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Rp ${"%,.0f".format(car!!.hargaPerHari)} / hari",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
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