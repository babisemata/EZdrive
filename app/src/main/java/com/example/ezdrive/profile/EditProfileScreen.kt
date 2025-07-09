package com.example.ezdrive.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ezdrive.model.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentUser: User?,
    onBack: () -> Unit,
    onSave: (username: String, phone: String, newImageUri: Uri?) -> Unit
) {
    // State untuk menampung perubahan dari input user
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Mengisi form dengan data awal saat currentUser sudah tersedia
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            username = currentUser.username ?: ""
            phoneNumber = currentUser.no_hp ?: ""
        }
    }
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.AccountCircle)


    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (currentUser == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model           = selectedImageUri ?: currentUser.profilePicture,
                    contentDescription = "Profile Picture",
                    placeholder     = placeholderPainter,
                    error           = placeholderPainter,
                    modifier        = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ,
                    contentScale    = ContentScale.Crop
                )



                Spacer(modifier = Modifier.height(8.dp))
                Text("Ketuk gambar untuk mengganti")

                Spacer(modifier = Modifier.height(32.dp))

                // --- INI BAGIAN PENTINGNYA ---
                // Tampilkan input field ini hanya jika rolenya "user"
                if (currentUser.role == "user") {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Nomor HP") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onSave(username, phoneNumber, selectedImageUri) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simpan Perubahan")
                }
            }
        }
    }
}