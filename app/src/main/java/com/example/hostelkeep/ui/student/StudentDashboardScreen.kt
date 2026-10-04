package com.example.hostelkeep.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.DashboardCard
import com.example.hostelkeep.components.ProfileHeader
import com.example.hostelkeep.components.SectionHeader
import com.example.hostelkeep.viewmodel.AuthViewModel
import com.example.hostelkeep.viewmodel.StudentViewModel

@Composable
fun StudentDashboardScreen(
    authViewModel: AuthViewModel,
    studentViewModel: StudentViewModel = viewModel(),
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val student by studentViewModel.student.collectAsState()
    val hostel by studentViewModel.hostel.collectAsState()
    val room by studentViewModel.room.collectAsState()
    val bed by studentViewModel.bed.collectAsState()
    val loading by studentViewModel.loading.collectAsState()

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
                title = "Student Portal",
                onNotificationClick = { onNavigate("notification_screen") },
                actions = {
                    IconButton(onClick = { onNavigate("student_profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val studentName = student?.name.takeIf { !it.isNullOrEmpty() } ?: currentUser?.name ?: "Student"
            val studentRoll = student?.rollNo.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val studentDept = student?.department.takeIf { !it.isNullOrEmpty() } ?: "CSE"
            val studentYear = student?.year?.toString() ?: "1"
            val studentEmail = student?.email.takeIf { !it.isNullOrEmpty() } ?: currentUser?.email ?: ""
            
            val hostelName = hostel?.name ?: student?.hostelId.takeIf { !it.isNullOrEmpty() }
            val roomNo = room?.roomNo?.ifEmpty { room?.roomNumber } ?: student?.roomNo.takeIf { !it.isNullOrEmpty() }
            val bedNo = bed?.bedNo?.ifEmpty { bed?.bedNumber } ?: student?.bedNo.takeIf { !it.isNullOrEmpty() }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeader(
                    name = studentName,
                    subtitle = "Roll No: $studentRoll • Dept: $studentDept • Year: $studentYear",
                    email = studentEmail
                )

                // Hostel & Room Allocation Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Hostel & Room Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        if (hostelName.isNullOrEmpty() || roomNo.isNullOrEmpty()) {
                            Text(
                                "Room not allocated yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Hostel: $hostelName", style = MaterialTheme.typography.bodyMedium)
                            Text("Room No: $roomNo${if (!bedNo.isNullOrEmpty()) " (Bed: $bedNo)" else ""}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onNavigate("emergency_screen") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Warning, null)
                        Spacer(Modifier.width(8.dp))
                        Text("SOS Emergency")
                    }
                }

                DashboardCard(
                    title = "My Attendance",
                    value = "View attendance records",
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onNavigate("student_attendance") }
                )
                DashboardCard(
                    title = "Fee Dues & Receipts",
                    value = "View fee records",
                    icon = Icons.Default.Payment,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { onNavigate("fee_dashboard") }
                )
                DashboardCard(
                    title = "Mess Menu & Rating",
                    value = "View daily menu",
                    icon = Icons.Default.Restaurant,
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = { onNavigate("mess_dashboard") }
                )

                SectionHeader(title = "Quick Actions")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { onNavigate("apply_leave") }, modifier = Modifier.weight(1f)) {
                        Text("Apply Leave")
                    }
                    OutlinedButton(onClick = { onNavigate("apply_outpass") }, modifier = Modifier.weight(1f)) {
                        Text("Get Outpass")
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { onNavigate("create_complaint") }, modifier = Modifier.weight(1f)) {
                        Text("Report Issue")
                    }
                    OutlinedButton(onClick = { onNavigate("hostel_rules") }, modifier = Modifier.weight(1f)) {
                        Text("Hostel Rules")
                    }
                }
            }
        }
    }
}
