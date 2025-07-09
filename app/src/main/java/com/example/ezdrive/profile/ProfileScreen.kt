package com.example.ezdrive.profile

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(
    userName: String = "Your name",
    userEmail: String = "yourname@gmail.com",
    userPhone: String? = null,
    userRole: String = "User",
    userLocation: String = "USA",
    // terima data gambar sebagai ByteArray jika ada
    profilePictureData: ByteArray? = null,
    onEdit: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Jika ada ByteArray, pakai sebagai model; jika tidak, gunakan avatar default
            val placeholderPainter = rememberVectorPainter(image = Icons.Default.AccountCircle)

            AsyncImage(
                // Model HANYA berisi data gambar utama Anda
                model = profilePictureData,

                contentDescription = "Profile Picture",

                // Placeholder akan ditampilkan saat model sedang loading atau null
                placeholder = placeholderPainter,

                // Error akan ditampilkan jika gagal memuat model
                error = placeholderPainter,

                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable { onEdit() },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userName,
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = userEmail,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileItem(label = "Name", value = userName)
            ProfileItem(label = "Email account", value = userEmail)
            ProfileItem(label = "Mobile number", value = userPhone ?: "Add number")
            ProfileItem(label = "Location", value = userLocation)
            ProfileItem(label = "Role", value = userRole)

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Profile")
            }
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onBack) {
                Text("Back to Home")
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}
