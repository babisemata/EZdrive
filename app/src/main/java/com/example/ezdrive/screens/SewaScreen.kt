package com.example.ezdrive.screens

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Booking
import com.example.ezdrive.service.SessionManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class BookingItem(
    val id: Int,
    val carName: String,
    val dateRange: String,
    val price: String,
    val status: String,
    val imageData: ByteArray
)

fun Booking.toBookingItem(): BookingItem {
    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val formattedPrice = formatRupiah.format(this.totalPrice).replace(",00", "")
    return BookingItem(
        id = this.bookingId,
        carName = this.carName,
        dateRange = "${this.startDate} - ${this.endDate}",
        price = formattedPrice,
        status = this.status,
        imageData = this.carImage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SewaScreen(onPayClicked: (Int) -> Unit) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val sessionManager = remember { SessionManager(context) }

    var bookingList by remember { mutableStateOf<List<BookingItem>>(emptyList()) }
    var selectedBooking by remember { mutableStateOf<BookingItem?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Load data
    LaunchedEffect(Unit) {
        val email = sessionManager.getUserEmail()
        if (email != null) {
            val userId = dbHelper.getUserIdByEmail(email)
            if (userId != -1) {
                val bookingsFromDb = dbHelper.getBookingsForUser(userId)
                bookingList = bookingsFromDb.map { it.toBookingItem() }
            }
        }
    }

    LaunchedEffect(selectedBooking?.id) {
        Log.d("SewaScreen", "selectedBooking: $selectedBooking")
        if (selectedBooking != null) {
            sheetState.show()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Sewa", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        if (bookingList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Anda belum memiliki riwayat sewa.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookingList) { booking ->
                    BookingCard(
                        booking = booking,
                        onClick = {
                            if (booking.status.equals("Confirmed", ignoreCase = true) || booking.status.equals("Paid", ignoreCase = true)) {                                Log.d("BookingClick", "Clicked booking ID: ${booking.id}")
                                selectedBooking = booking
                            }
                        }
                    )
                }
            }
        }

        // Bottom Sheet
        if (selectedBooking != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        selectedBooking = null
                    }
                },
                sheetState = sheetState
            ) {
                BookingDetailSheet(
                    booking = selectedBooking!!,
                    onPayClicked = {
                        onPayClicked(selectedBooking!!.id)
                        scope.launch {
                            sheetState.hide()
                            selectedBooking = null
                        }
                    },
                    onClose = {
                        scope.launch {
                            sheetState.hide()
                            selectedBooking = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingItem, onClick: () -> Unit) {
    val bitmap = remember(booking.imageData) {
        BitmapFactory.decodeByteArray(booking.imageData, 0, booking.imageData.size)?.asImageBitmap()
    }

    val isClickable = booking.status.equals("Confirmed", ignoreCase = true) || booking.status.equals("Paid", ignoreCase = true)

    val cardModifier = if (isClickable) {
        Modifier
            .fillMaxWidth()
            .clickable {
                Log.d("BookingCard", "Clicked: ${booking.id}")
                onClick()
            }
    } else {
        Modifier.fillMaxWidth()
    }


    Card(
        modifier = cardModifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            bitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = booking.carName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.carName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = booking.dateRange,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = booking.price,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = booking.status,
                fontWeight = FontWeight.Medium,
                color = if (booking.status.equals("Confirmed", ignoreCase = true)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Composable
fun BookingDetailSheet(
    booking: BookingItem,
    onPayClicked: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Detail Booking", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Divider()
        DetailRow(label = "Mobil", value = booking.carName)
        DetailRow(label = "Tanggal", value = booking.dateRange)
        DetailRow(label = "Total Harga", value = booking.price)
        DetailRow(label = "Status", value = booking.status)

        if (booking.status.equals("Confirmed", ignoreCase = true)) {
            Button(onClick = onPayClicked, modifier = Modifier.fillMaxWidth()) {
                Text("Bayar Sekarang")
            }
        }

        TextButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
            Text("Tutup")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
    }
}
