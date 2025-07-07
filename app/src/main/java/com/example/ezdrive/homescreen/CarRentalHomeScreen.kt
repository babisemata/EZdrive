package com.example.ezdrive.homescreen

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezdrive.R
import com.example.ezdrive.theme.EZDriveTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// --- DATA CLASSES ---
data class CarItem(
    val id: Int,
    val name: String,
    val type: String,
    val pricePerDay: String,
    val imageUrl: Int,
    val rating: Float,
    val seats: Int,
    val transmission: String
)

data class CarCategory(
    val id: String,
    val name: String,
    val iconRes: ImageVector
)

// --- DUMMY DATA ---
val carCategoriesData = listOf(
    CarCategory("all", "Semua", Icons.Filled.DirectionsCar),
    CarCategory("popular", "Populer", Icons.Filled.Star),
    CarCategory("suv", "SUV", Icons.Filled.DirectionsCar),
    CarCategory("sedan", "Sedan", Icons.Filled.DriveEta),
    CarCategory("mpv", "MPV", Icons.Filled.DirectionsBus),
    CarCategory("hatchback", "Hatchback", Icons.Filled.ElectricCar)
)

val featuredCarsData = listOf(
    CarItem(1, "Toyota Avanza", "MPV", "Rp 350.000", R.drawable.img_car_avanza, 4.5f, 7, "Manual"),
    CarItem(2, "Honda HR-V", "SUV", "Rp 550.000", R.drawable.img_car_hrv, 4.8f, 5, "Automatic"),
    CarItem(3, "Suzuki Ertiga", "MPV", "Rp 300.000", R.drawable.img_car_ertiga, 4.3f, 7, "Manual"),
    CarItem(4, "Mitsubishi Xpander", "MPV", "Rp 400.000", R.drawable.img_car_xpander, 4.6f, 7, "Automatic"),
    CarItem(5, "Daihatsu Terios", "SUV", "Rp 450.000", R.drawable.img_car_terios, 4.4f, 7, "Manual"),
    CarItem(6, "Honda Brio", "Hatchback","Rp 250.000", R.drawable.img_car_brio, 4.2f, 5, "Automatic")
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarRentalHomeScreen(onCarClicked: (CarItem) -> Unit, onProfile: () -> Unit) {
    var selectedCategory by remember { mutableStateOf<CarCategory?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val halfScreenWidth = configuration.screenWidthDp.dp / 2

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(halfScreenWidth),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomStart = 24.dp)
            ) {
                NavigationPaneContent(
                    userName = "Nolan Mahotama",
                    userLocation = "Makassar",
                    onProfile = { onProfile},
                    profilePicRes = R.drawable.img_profile_small
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("EZ Drive", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Filled.Search, contentDescription = "Cari Mobil")
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { LocationAndDateCard() }
                item {
                    CarCategoriesSection(
                        categories = carCategoriesData,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { category ->
                            selectedCategory = category
                        }
                    )
                }
                item {
                    val carsToShow = when (selectedCategory?.id) {
                        "all", null -> featuredCarsData
                        "popular" -> featuredCarsData.sortedByDescending { it.rating }
                        else -> featuredCarsData.filter { it.type.equals(selectedCategory?.name, ignoreCase = true) }
                    }
                    val titleText = when (selectedCategory?.id) {
                        "popular" -> "Pilihan Populer"
                        "all", null -> "Semua Mobil"
                        else -> "Mobil ${selectedCategory?.name}"
                    }
                    FeaturedCarsSection(
                        title = titleText,
                        cars = carsToShow,
                        filterCategory = selectedCategory,
                        onCarClicked = onCarClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationPaneContent(userName: String, userLocation: String, profilePicRes: Int, onProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = "Lokasi")
                    Spacer(Modifier.width(4.dp))
                    Text(text = userLocation, style = MaterialTheme.typography.bodySmall)
                }
            }
            Image(
                painter = painterResource(id = profilePicRes),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(24.dp))
        Divider()
        Spacer(Modifier.height(8.dp))
        NavigationDrawerItem(
            label = { Text("Home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            selected = true,
            onClick = { /* TODO */ },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Bookings") },
            icon = { Icon(Icons.Filled.Bookmark, contentDescription = null) },
            selected = false,
            onClick = { /* TODO */ },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Profile") },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            selected = false,
            onClick = { /* TODO */ },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Profile") },
            icon = { Icon(Icons.Filled.PersonOutline, contentDescription = null) },
            selected = false,
            onClick = { onProfile },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
            text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationAndDateCard() {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    var selectedDate by remember { mutableStateOf(today) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mau sewa mobil dimana?", fontWeight = FontWeight.Bold)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: location picker */ }
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Outlined.LocationOn, contentDescription = "Lokasi")
                Spacer(Modifier.width(12.dp))
                Text("Pilih Lokasi Penjemputan", Modifier.weight(1f))
                Icon(Icons.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Divider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: date picker */ }
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Outlined.DateRange, contentDescription = "Tanggal")
                Spacer(Modifier.width(12.dp))
                Text(selectedDate.format(formatter), Modifier.weight(1f))
                Icon(Icons.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp))
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
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else Color.Transparent,
                            shape = CircleShape
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = category.iconRes,
                        contentDescription = category.name,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = category.name,
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
    filterCategory: CarCategory?,
    onCarClicked: (CarItem) -> Unit
) {
    Column {
        SectionTitle(title)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(cars) { car ->
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .clickable { onCarClicked(car) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = car.imageUrl),
                            contentDescription = car.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(120.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        )
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(car.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(car.pricePerDay, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700))
                                Spacer(Modifier.width(4.dp))
                                Text(car.rating.toString(), style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.weight(1f))
                                Text("${car.seats} seats", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(car.transmission, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}







