package com.example.ezdrive.homescreen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ezdrive.R
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Car
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.NumberFormat
import java.util.Locale

// --- DATA MODELS ---
data class CarItem(
    val carFromDb: Car,
    val name: String,
    val type: String,
    val pricePerDay: String,
    val imageData: ByteArray,
    val rating: Float,
    val seats: Int,
    val transmission: String
)

data class CarCategory(
    val id: String,
    val name: String,
    val icon: ImageVector
)

fun Car.toCarItem(): CarItem {
    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val formattedPrice = formatRupiah.format(this.hargaPerHari).replace(",00", "")

    return CarItem(
        carFromDb = this,
        name = "${this.merk} ${this.model}",
        type = this.category,
        pricePerDay = "$formattedPrice/hari",
        imageData = this.foto,
        rating = 4.5f,
        seats = this.kapasitas,
        transmission = this.transmission
    )
}

// --- DUMMY DATA ---
private val carCategories = listOf(
    CarCategory("all", "Semua", Icons.Filled.DirectionsCar),
    CarCategory("popular", "Populer", Icons.Filled.Star),
    CarCategory("suv", "SUV", Icons.Filled.DirectionsCar),
    CarCategory("sedan", "Sedan", Icons.Filled.DriveEta),
    CarCategory("mpv", "MPV", Icons.Filled.DirectionsBus),
    CarCategory("hatchback", "Hatchback", Icons.Filled.ElectricCar)
)


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarRentalHomeScreen(
    onNavigate: (String) -> Unit,
    onCarClicked: (CarItem) -> Unit,
    onProfile: () -> Unit

) {
    val context = LocalContext.current
    var carList by remember { mutableStateOf<List<CarItem>>(emptyList()) }
    val dbHelper = remember { DBHelper(context) }
    LaunchedEffect(Unit) {
        val carsFromDb = dbHelper.getAllCars()
        carList = carsFromDb.map { it.toCarItem() }
    }
    var selectedCategory by remember { mutableStateOf<CarCategory?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("EZ Drive", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { onNavigate("search") }) {
                        Icon(Icons.Filled.Search, contentDescription = "Cari Mobil")
                    }

                    IconButton(onClick = { /* notifications */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi")
                    }
                    IconButton(onClick = onProfile) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { LocationAndDateCard(
                onMapClick = { onNavigate("map") }
            ) }

            item {
                CarCategoriesSection(
                    categories = carCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            item {
                FeaturedCarsSection(
                    title = "Semua Mobil",
                    cars = carList, // Gunakan carList dari state
                    onCarClicked = onCarClicked
                )
            }
        }
    }
}

// --- REUSABLE SECTIONS ---
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun LocationAndDateCard(
    onMapClick: () -> Unit
) {
    val context = LocalContext.current

    // State tanggal (tetap sama)
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    var selectedDate by remember { mutableStateOf(today) }

    // State dialog & lokasi
    var showLocationDialog by remember { mutableStateOf(false) }
    var pickupLocation by remember { mutableStateOf("Cabang Utama") }

    // Koordinat Cabang Utama di Gianyar (Jln. Patih Jelantik No.102)
    val lat = -8.5141    // ganti dengan latitude akurat
    val lng = 115.2628   // ganti dengan longitude akurat
    val label = Uri.encode("EZ Drive Cabang Utama")

    // Dialog Pilih Lokasi
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Pilih Lokasi") },
            text = {
                Column {
                    TextButton(onClick = {
                        pickupLocation = "Cabang Utama"
                        showLocationDialog = false
                        onMapClick()
                    }) {
                        Text("Cabang Utama")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // UI Card
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mau sewa mobil dimana?", fontWeight = FontWeight.Bold)

            // Bar lokasi (buka dialog)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLocationDialog = true }
                    .padding(vertical = 8.dp)
            )  {
                Icon(Icons.Outlined.LocationOn, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(pickupLocation, Modifier.weight(1f))
                Icon(Icons.Filled.ArrowForwardIos, contentDescription = null, Modifier.size(16.dp))
            }
        }
    }
}



@Composable
fun CarCategoriesSection(
    categories: List<CarCategory>,
    selectedCategory: CarCategory?,
    onCategorySelected: (CarCategory) -> Unit
) {
    Column {
        SectionTitle("Kategori Mobil")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(categories) { category ->
                val isSelected = category.id == selectedCategory?.id
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onCategorySelected(category) }
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(0.1f)
                            else Color.Transparent,
                            CircleShape
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        category.icon,
                        contentDescription = category.name,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedCarsSection(
    title: String,
    cars: List<CarItem>,
    onCarClicked: (CarItem) -> Unit
) {
    Column {
        SectionTitle(title)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(cars) { car ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .clickable { onCarClicked(car) }
                ) {
                    Column {
                        // Gunakan Coil untuk memuat gambar dari ByteArray (BLOB)
                        AsyncImage(
                            model = car.imageData,
                            contentDescription = car.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.height(120.dp).fillMaxWidth()
                        )
                        Column(Modifier.padding(12.dp)) {
                            Text(car.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(car.pricePerDay, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700))
                                Spacer(Modifier.width(4.dp))
                                Text(text = "${car.rating}", style = MaterialTheme.typography.bodySmall)

                                Spacer(Modifier.weight(1f))

                                Text(text = "${car.seats} seats", style = MaterialTheme.typography.bodySmall)

                            }
                            Spacer(Modifier.height(4.dp))
                            Text(car.transmission, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
