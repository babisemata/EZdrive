package com.example.ezdrive.detailscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbAuto
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ezdrive.model.Car
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    car: Car, // Terima seluruh objek Car
    onBack: () -> Unit,
    onBooking: () -> Unit // Ganti nama callback agar konsisten
) {
    // Format harga ke Rupiah
    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val formattedPrice = formatRupiah.format(car.hargaPerHari ?: 0.0).replace(",00", "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Mobil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Bagian Kiri: Menampilkan Harga
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Harga per hari", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = formattedPrice, // Asumsi variabel ini ada
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Bagian Kanan: Tombol Aksi
                Button(
                    enabled = car.isAvailable,
                    onClick = onBooking
                ) {
                    Text(if (car.isAvailable) "Sewa Sekarang" else "Telah Disewa")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Gambar Mobil
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = car.foto,
                    contentDescription = "${car.merk} ${car.model}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
                if (!car.isAvailable) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak Tersedia", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Konten Detail
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Judul dan Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${car.merk} ${car.model}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("4.5", fontWeight = FontWeight.SemiBold) // Rating sementara
                }

                // Deskripsi
                Text(
                    text = "Mobil ${car.category ?: ""} ini adalah pilihan terbaik untuk perjalanan Anda. Dengan transmisi ${car.transmission ?: "N/A"} dan kapasitas ${car.kapasitas ?: 0} kursi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Divider()

                // Spesifikasi
                Text("Spesifikasi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    SpecificationItem(icon = Icons.Filled.WbAuto, label = "Transmisi", value = car.transmission ?: "N/A")
                    SpecificationItem(icon = Icons.Filled.Chair, label = "Kapasitas", value = "${car.kapasitas ?: 0} Kursi")
                    SpecificationItem(icon = Icons.Filled.CalendarToday, label = "Tahun", value = car.tahun?.toString() ?: "N/A")
                }
            }
        }
    }
}

// Helper Composable untuk item spesifikasi
@Composable
private fun SpecificationItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}