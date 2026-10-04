package com.example.hostelkeep.ui.parent

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.DashboardCard

@Composable
fun ParentDashboardScreen(onNavigate: (String) -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Parent Portal") }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCard(title = "Ward Attendance", value = "No attendance records available", icon = Icons.Default.CheckCircle, color = MaterialTheme.colorScheme.primary, onClick = { onNavigate("student_attendance") })
            DashboardCard(title = "Fee Status", value = "No fee records available", icon = Icons.Default.Payment, color = MaterialTheme.colorScheme.secondary, onClick = { onNavigate("fee_dashboard") })
            DashboardCard(title = "Leave Requests", value = "No pending leave requests", icon = Icons.Default.EventNote, color = MaterialTheme.colorScheme.tertiary, onClick = { onNavigate("leave_dashboard") })
        }
    }
}
