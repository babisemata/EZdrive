package com.example.ezdrive.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezdrive.R
import com.example.ezdrive.navigationbar.NavItem

data class BookingItem(
    val id: Int,
    val carName: String,
    val dateRange: String,
    val price: String,
    val status: BookingStatus,
    val imageRes: Int
)

enum class BookingStatus { Pending, Confirmed, Cancelled }

val sampleBookings = listOf(
    BookingItem(1, "Toyota Avanza", "10 Jul 2025 - 12 Jul 2025", "Rp 700.000", BookingStatus.Confirmed, R.drawable.img_car_avanza),
    BookingItem(2, "Honda HR-V", "15 Jul 2025 - 17 Jul 2025", "Rp 1.100.000", BookingStatus.Pending, R.drawable.img_car_hrv),
    BookingItem(3, "Suzuki Ertiga", "20 Jul 2025 - 22 Jul 2025", "Rp 600.000", BookingStatus.Cancelled, R.drawable.img_car_ertiga)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SewaScreen(
    currentRoute: String,
    onNavigate: (route: String) -> Unit,
    bookings: List<BookingItem> = sampleBookings,
    onBookingClick: (BookingItem) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Bookings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(NavItem.Home.route) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: filter action */ }) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { paddingValues ->
        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada booking",
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
                items(bookings) { booking ->
                    BookingCard(booking = booking, onClick = { onBookingClick(booking) })
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = booking.imageRes),
                contentDescription = booking.carName,
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterVertically)
            )
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
                text = when (booking.status) {
                    BookingStatus.Pending -> "Pending"
                    BookingStatus.Confirmed -> "Konfirmasi"
                    BookingStatus.Cancelled -> "Batal"
                },
                fontWeight = FontWeight.Medium,
                color = when (booking.status) {
                    BookingStatus.Pending -> MaterialTheme.colorScheme.error
                    BookingStatus.Confirmed -> MaterialTheme.colorScheme.primary
                    BookingStatus.Cancelled -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
