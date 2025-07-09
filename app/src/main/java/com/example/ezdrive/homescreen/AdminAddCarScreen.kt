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

@Composable
fun AdminAddCarScreen(
    onCarAdded: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dbHelper = remember { DBHelper(context) }

    // --- State untuk setiap field input ---
    var merk by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var kapasitas by remember { mutableStateOf("") }
    var hargaPerHari by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var transmission by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // --- Image Picker ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // --- UI Form ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Agar bisa di-scroll jika form panjang
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Tambah Mobil Baru", style = MaterialTheme.typography.headlineSmall)

        // Preview gambar yang dipilih
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Gambar Mobil Terpilih",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
        }

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text(if (imageUri != null) "Ganti Gambar" else "Pilih Gambar")
        }

        OutlinedTextField(value = merk, onValueChange = { merk = it }, label = { Text("Merk") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tahun, onValueChange = { tahun = it }, label = { Text("Tahun") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = kapasitas, onValueChange = { kapasitas = it }, label = { Text("Kapasitas Kursi") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = hargaPerHari, onValueChange = { hargaPerHari = it }, label = { Text("Harga/Hari") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori (e.g., SUV, MPV)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = transmission,
            onValueChange = { transmission = it }, // 'C' must be capitalized
            label = { Text("Transmisi (e.g., Automatic)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tersedia", modifier = Modifier.weight(1f))
            Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                // Validasi input
                if (merk.isBlank() || model.isBlank() || imageUri == null) {
                    Toast.makeText(context, "Merk, Model, dan Gambar wajib diisi", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val imageByteArray = uriToByteArray(context, imageUri!!)

                if (imageByteArray == null) {
                    Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                scope.launch {
                    val newCar = Car(
                        carid = 0, // Dibuat otomatis oleh DB
                        merk = merk,
                        model = model,
                        tahun = tahun.toIntOrNull() ?: 0,
                        kapasitas = kapasitas.toIntOrNull() ?: 0,
                        hargaPerHari = hargaPerHari.toDoubleOrNull() ?: 0.0,
                        foto = imageByteArray,
                        category = category,
                        transmission = transmission,
                        isAvailable = isAvailable,
                        id_user = 1 // Ganti dengan ID admin yang login jika perlu
                    )

                    val success = dbHelper.addCar(newCar)

                    launch(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "Mobil berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                            onCarAdded() // Kembali ke layar sebelumnya
                        } else {
                            Toast.makeText(context, "Gagal menambahkan mobil.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Mobil")
        }
    }
}

// Helper function untuk mengubah URI ke ByteArray
private fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.readBytes()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}