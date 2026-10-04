package com.example.hostelkeep.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.model.User
import com.example.hostelkeep.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: (UserRole) -> Unit
) {
    LaunchedEffect(true) {
        delay(1500)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            try {
                val db = FirebaseFirestore.getInstance()
                val doc = db.collection("users").document(firebaseUser.uid).get().await()
                val user = doc.toObject(User::class.java)
                val role = user?.role ?: UserRole.STUDENT
                onNavigateToDashboard(role)
            } catch (e: Exception) {
                onNavigateToDashboard(UserRole.STUDENT)
            }
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Hotel, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("HostelHub", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text("Smart Hostel Management System", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
        }
    }
}
