package com.example.hostelkeep.ui.outpass

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.StatusChip
import com.example.hostelkeep.model.RequestStatus
import com.example.hostelkeep.model.User
import com.example.hostelkeep.model.UserRole
import com.example.hostelkeep.viewmodel.OutpassViewModel

@Composable
fun OutpassDashboardScreen(
    currentUser: User?,
    outpassViewModel: OutpassViewModel, 
    onNavigate: (String) -> Unit
) {
    LaunchedEffect(currentUser) {
        outpassViewModel.loadData(currentUser)
    }
    
    val outpasses by outpassViewModel.outpasses.collectAsState()
    val isStudent = currentUser?.role == UserRole.STUDENT || currentUser?.role == UserRole.PARENT
    val isWarden = currentUser?.role == UserRole.WARDEN || currentUser?.role == UserRole.ADMIN
    val isSecurity = currentUser?.role == UserRole.SECURITY

    Scaffold(
        topBar = { AppTopBar(title = "Outpass Management") },
        floatingActionButton = {
            if (isStudent) {
                FloatingActionButton(onClick = { onNavigate("apply_outpass") }) {
                    Icon(Icons.Default.Add, contentDescription = "Apply Outpass")
                }
            } else if (isSecurity) {
                FloatingActionButton(onClick = { onNavigate("qr_scanner") }) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR")
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(outpasses) { op ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(op.studentName, style = MaterialTheme.typography.titleMedium)
                            StatusChip(
                                op.status.name,
                                when (op.status) {
                                    RequestStatus.PENDING -> Color(245, 158, 11)
                                    RequestStatus.APPROVED -> Color(16, 185, 129)
                                    RequestStatus.REJECTED -> Color(239, 68, 68)
                                    RequestStatus.CANCELLED -> Color.Gray
                                    RequestStatus.EXPIRED -> Color.Red
                                    RequestStatus.CHECKED_OUT -> Color.Blue
                                    RequestStatus.RETURNED -> Color(16, 185, 129)
                                }
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Destination: ${op.destination}")
                        Text("Out: ${op.outTime} • In: ${op.inTime}")
                        Text("Reason: ${op.reason}")
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (isWarden && op.status == RequestStatus.PENDING) {
                                Button(onClick = { outpassViewModel.updateStatus(op.id, RequestStatus.APPROVED, currentUser?.id ?: "unknown") }) {
                                    Text("Approve")
                                }
                                OutlinedButton(onClick = { outpassViewModel.updateStatus(op.id, RequestStatus.REJECTED, currentUser?.id ?: "unknown") }) {
                                    Text("Reject")
                                }
                            }
                            if (isStudent && (op.status == RequestStatus.APPROVED || op.status == RequestStatus.CHECKED_OUT)) {
                                OutlinedButton(onClick = { onNavigate("qr_outpass/${op.id}") }) {
                                    Text("View QR Pass")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
