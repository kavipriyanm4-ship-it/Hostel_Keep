package com.example.hostelkeep.ui.warden

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.DashboardCard
import com.example.hostelkeep.components.SectionHeader

@Composable
fun WardenDashboardScreen(onNavigate: (String) -> Unit, onLogout: () -> Unit) {
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
                title = "Warden Dashboard",
                onNotificationClick = { onNavigate("notification_screen") },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardCard(
                title = "Room Management",
                value = "View & Manage Rooms",
                icon = Icons.Default.MeetingRoom,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onNavigate("rooms_list") }
            )
            DashboardCard(
                title = "Leave Requests Approval",
                value = "2 Pending",
                icon = Icons.Default.EventNote,
                color = MaterialTheme.colorScheme.secondary,
                onClick = { onNavigate("leave_dashboard") }
            )
            DashboardCard(
                title = "Outpass Approvals",
                value = "1 Pending",
                icon = Icons.Default.QrCode,
                color = MaterialTheme.colorScheme.secondary,
                onClick = { onNavigate("outpass_dashboard") }
            )
            DashboardCard(
                title = "Daily Attendance",
                value = "Mark Attendance",
                icon = Icons.Default.CheckCircle,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = { onNavigate("attendance_dashboard") }
            )
            SectionHeader(title = "Warden Actions")
            Button(onClick = { onNavigate("warden_announcements") }, modifier = Modifier.fillMaxWidth()) {
                Text("Publish Announcement / Circular")
            }
        }
    }
}
