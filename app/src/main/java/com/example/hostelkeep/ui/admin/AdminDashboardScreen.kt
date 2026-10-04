package com.example.hostelkeep.ui.admin

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
import com.example.hostelkeep.viewmodel.StudentViewModel
import com.example.hostelkeep.viewmodel.RoomViewModel
import com.example.hostelkeep.viewmodel.ComplaintViewModel
import com.example.hostelkeep.viewmodel.FeeViewModel

@Composable
fun AdminDashboardScreen(
    studentViewModel: StudentViewModel,
    roomViewModel: RoomViewModel,
    complaintViewModel: ComplaintViewModel,
    feeViewModel: FeeViewModel,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val students by studentViewModel.students.collectAsState()
    val rooms by roomViewModel.rooms.collectAsState()
    val complaints by complaintViewModel.complaints.collectAsState()
    val fees by feeViewModel.fees.collectAsState()

    val totalOccupied = rooms.sumOf { it.occupied }
    val totalCapacity = rooms.sumOf { it.capacity }
    val pendingComplaints = complaints.count { it.status != com.example.hostelkeep.model.ComplaintStatus.RESOLVED }
    val pendingFees = fees.count { it.status == com.example.hostelkeep.model.FeeStatus.PENDING || it.status == com.example.hostelkeep.model.FeeStatus.OVERDUE }

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
                title = "Admin Dashboard",
                onNotificationClick = { onNavigate("notification_screen") },
                actions = {
                    IconButton(onClick = { onNavigate("admin_settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
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
                title = "Total Students Enrolled",
                value = "${students.size}",
                icon = Icons.Default.People,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onNavigate("students_list") }
            )
            DashboardCard(
                title = "Room Occupancy",
                value = "$totalOccupied / $totalCapacity Beds",
                icon = Icons.Default.MeetingRoom,
                color = MaterialTheme.colorScheme.secondary,
                onClick = { onNavigate("rooms_list") }
            )
            DashboardCard(
                title = "Pending Complaints",
                value = "$pendingComplaints Issues",
                icon = Icons.Default.Build,
                color = MaterialTheme.colorScheme.error,
                onClick = { onNavigate("complaint_dashboard") }
            )
            DashboardCard(
                title = "Pending Fee Dues",
                value = "$pendingFees Students",
                icon = Icons.Default.Payment,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = { onNavigate("fee_dashboard") }
            )

            SectionHeader(title = "Quick Management", actionText = "View Reports", onActionClick = { onNavigate("reports_screen") })
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onNavigate("add_student") }, modifier = Modifier.weight(1f)) {
                    Text("Add Student")
                }
                Button(onClick = { onNavigate("room_allocation") }, modifier = Modifier.weight(1f)) {
                    Text("Allocate Room")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onNavigate("rooms_list") }, modifier = Modifier.weight(1f)) {
                    Text("Room Management")
                }
                OutlinedButton(onClick = { onNavigate("emergency_dashboard") }, modifier = Modifier.weight(1f)) {
                    Text("Emergency SOS")
                }
            }
        }
    }
}
