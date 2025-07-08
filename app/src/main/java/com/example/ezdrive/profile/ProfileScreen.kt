package com.example.ezdrive.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    userEmail: String,
    userRole: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Profile", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp))
            Text("Email: $userEmail")
            Text("Role: $userRole")

            Spacer(Modifier.height(24.dp))

            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Home")
            }
        }
    }
}
