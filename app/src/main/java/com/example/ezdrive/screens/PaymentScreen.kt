package com.example.ezdrive.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Booking
import com.example.ezdrive.model.Payment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    bookingId: Int,
    onPaymentSuccess: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val scope = rememberCoroutineScope()
    var booking by remember { mutableStateOf<Booking?>(null) }

    LaunchedEffect(bookingId) {
        booking = dbHelper.getBookingById(bookingId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pembayaran") }) }
    ) { padding ->
        if (booking == null) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Ringkasan Pesanan", style = MaterialTheme.typography.titleLarge)
                Text("Mobil: ${booking!!.carName}")
                Text("Tanggal: ${booking!!.startDate} s/d ${booking!!.endDate}")
                Text(
                    "Total Bayar: Rp ${"%,.0f".format(booking!!.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Divider()
                // Di sini bisa ditambahkan pilihan metode pembayaran
                Button(
                    onClick = {
                        scope.launch {
                            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            val newPayment = Payment(0, booking!!.bookingId, booking!!.totalPrice, currentDate, "Paid")

                            // Simpan pembayaran dan update status booking
                            if (dbHelper.addPayment(newPayment)) {
                                dbHelper.updateBookingStatus(booking!!.bookingId, "Paid")
                                Toast.makeText(context, "Pembayaran Berhasil!", Toast.LENGTH_LONG).show()
                                onPaymentSuccess()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Bayar Sekarang")
                }
            }
        }
    }
}