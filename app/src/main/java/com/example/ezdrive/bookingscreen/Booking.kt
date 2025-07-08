package com.example.ezdrive.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ezdrive.R

// Data class untuk booking
data class BookingItem(
    val id: Int,
    val carName: String,
    val date: String,
    val status: String
)

// Contoh data booking
val sampleBookings = listOf(
    BookingItem(1, "Toyota Avanza", "10 Jul 2025", "Confirmed"),
    BookingItem(2, "Honda HR-V", "12 Jul 2025", "Pending"),
    BookingItem(3, "Suzuki Ertiga", "15 Jul 2025", "Cancelled")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    currentRoute: String,
    onNavigate: (route: String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.title_bookings), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = { onNavigate.invoke(NavItem.Home.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bookings),
                            contentDescription = stringResource(R.string.desc_bookings_icon)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleBookings) { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = booking.carName, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = booking.date)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = booking.status)
                    }
                }
            }
        }
    }
}
