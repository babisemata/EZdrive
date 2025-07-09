package com.example.ezdrive.screens.admin

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.Car
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditCarScreen(
    carId: Int,
    onCarUpdated: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dbHelper = remember { DBHelper(context) }

    // State untuk menampung semua data mobil dari form
    var merk by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var kapasitas by remember { mutableStateOf("") }
    var hargaPerHari by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var transmission by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }

    // State untuk gambar
    var existingImage by remember { mutableStateOf<ByteArray?>(null) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    // State untuk loading
    var isLoading by remember { mutableStateOf(true) }

    // Mengambil data mobil dari DB saat layar pertama kali dibuka
    LaunchedEffect(carId) {
        val car = dbHelper.getCarById(carId)
        if (car != null) {
            // Gunakan operator Elvis (?:) untuk memberikan nilai default "" jika data null
            merk = car.merk ?: ""
            model = car.model ?: ""
            tahun = car.tahun?.toString() ?: ""
            kapasitas = car.kapasitas?.toString() ?: ""
            hargaPerHari = car.hargaPerHari?.toString() ?: ""
            category = car.category ?: ""
            transmission = car.transmission ?: ""
            isAvailable = car.isAvailable
            existingImage = car.foto
        }
        isLoading = false
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        newImageUri = uri
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Edit Detail Mobil", style = MaterialTheme.typography.headlineSmall)

            // Menampilkan gambar
            AsyncImage(
                model = newImageUri ?: existingImage,
                contentDescription = "Foto Mobil",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )

            Button(onClick = { imagePickerLauncher.launch("*/*") }) {
                Text("Ganti Gambar")
            }

            OutlinedTextField(value = merk, onValueChange = { merk = it }, label = { Text("Merk") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tahun, onValueChange = { tahun = it }, label = { Text("Tahun") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = kapasitas, onValueChange = { kapasitas = it }, label = { Text("Kapasitas Kursi") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = hargaPerHari, onValueChange = { hargaPerHari = it }, label = { Text("Harga/Hari") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori (e.g., SUV, MPV)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = transmission, onValueChange = { transmission = it }, label = { Text("Transmisi (e.g., Automatic)") }, modifier = Modifier.fillMaxWidth())

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tersedia", modifier = Modifier.weight(1f))
                Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val fotoByteArray = newImageUri?.let { uriToByteArray(context, it) } ?: existingImage

                    if (fotoByteArray != null) {
                        scope.launch {
                            val updatedCar = Car(
                                carid = carId,
                                merk = merk,
                                model = model,
                                tahun = tahun.toIntOrNull() ?: 0,
                                kapasitas = kapasitas.toIntOrNull() ?: 0,
                                hargaPerHari = hargaPerHari.toDoubleOrNull() ?: 0.0,
                                foto = fotoByteArray,
                                category = category,
                                transmission = transmission,
                                isAvailable = isAvailable,
                                id_user = 0 // Anda bisa sesuaikan ini jika perlu
                            )
                            val success = dbHelper.updateCar(updatedCar)
                            launch(Dispatchers.Main) {
                                if (success) {
                                    Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                    onCarUpdated() // Kembali ke layar sebelumnya
                                } else {
                                    Toast.makeText(context, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Gambar tidak valid", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}

// Helper function untuk mengubah URI ke ByteArray
private fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.readBytes()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}