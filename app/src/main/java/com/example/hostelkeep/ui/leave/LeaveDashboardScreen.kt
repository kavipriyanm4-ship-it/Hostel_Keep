package com.example.hostelkeep.ui.leave

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.StatusChip
import com.example.hostelkeep.model.RequestStatus
import com.example.hostelkeep.model.UserRole
import com.example.hostelkeep.viewmodel.AuthViewModel
import com.example.hostelkeep.viewmodel.LeaveViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaveDashboardScreen(
    leaveViewModel: LeaveViewModel, 
    authViewModel: AuthViewModel, 
    onNavigate: (String) -> Unit
) {
    val leaves by leaveViewModel.leaves.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isStudent = currentUser?.role == UserRole.STUDENT
    val isWardenOrAdmin = currentUser?.role == UserRole.WARDEN || currentUser?.role == UserRole.ADMIN

    LaunchedEffect(currentUser) {
        leaveViewModel.loadLeaves(currentUser)
    }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Scaffold(
        topBar = { AppTopBar(title = "Leave Requests", onMenuClick = { onNavigate("back") }) },
        floatingActionButton = {
            if (isStudent) {
                FloatingActionButton(onClick = { onNavigate("apply_leave") }) {
                    Icon(Icons.Default.Add, contentDescription = "Apply Leave")
                }
            }
        }
    ) { padding ->
        if (leaves.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No leave requests found.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                items(leaves) { leave ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(leave.studentName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                StatusChip(
                                    leave.status.name,
                                    when (leave.status) {
                                        RequestStatus.PENDING -> Color(245, 158, 11)
                                        RequestStatus.APPROVED -> Color(16, 185, 129)
                                        RequestStatus.REJECTED -> Color(239, 68, 68)
                                        RequestStatus.CANCELLED -> Color.Gray
                                        else -> Color.Gray
                                    }
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Type: ${leave.leaveType.name}", style = MaterialTheme.typography.bodyMedium)
                            Text("Dates: ${leave.fromDate} to ${leave.toDate}", style = MaterialTheme.typography.bodyMedium)
                            Text("Reason: ${leave.reason}", style = MaterialTheme.typography.bodyMedium)
                            Text("Requested: ${dateFormatter.format(Date(leave.requestedAt))}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            
                            if (leave.status == RequestStatus.PENDING) {
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (isWardenOrAdmin) {
                                        Button(onClick = { leaveViewModel.updateStatus(leave.id, RequestStatus.APPROVED, currentUser?.name ?: "Admin", "Approved") }) {
                                            Text("Approve")
                                        }
                                        OutlinedButton(onClick = { leaveViewModel.updateStatus(leave.id, RequestStatus.REJECTED, currentUser?.name ?: "Admin", "Rejected") }) {
                                            Text("Reject")
                                        }
                                    }
                                    if (isStudent && leave.studentId == currentUser?.id) {
                                        OutlinedButton(onClick = { leaveViewModel.cancelLeave(leave.id) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                                            Text("Cancel Request")
                                        }
                                    }
                                }
                            }
                            
                            if (leave.status != RequestStatus.PENDING && leave.status != RequestStatus.CANCELLED && leave.reviewedBy.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text("Reviewed by ${leave.reviewedBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
