package com.example.hostelkeep.ui.security

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.DashboardCard

@Composable
fun SecurityDashboardScreen(onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Security Gate Portal",
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCard(title = "QR Code Outpass Scanner", value = "Scan student pass", icon = Icons.Default.QrCodeScanner, color = MaterialTheme.colorScheme.primary, onClick = { onNavigate("qr_scanner") })
            DashboardCard(title = "Visitor Logbook", value = "No Active Visitors", icon = Icons.Default.List, color = MaterialTheme.colorScheme.secondary, onClick = { onNavigate("visitor_log") })
            DashboardCard(title = "Register New Visitor", value = "Entry pass generation", icon = Icons.Default.PersonAdd, color = MaterialTheme.colorScheme.tertiary, onClick = { onNavigate("register_visitor") })
        }
    }
}
