package com.example.hostelkeep.ui.complaints

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.model.ComplaintStatus
import com.example.hostelkeep.viewmodel.ComplaintViewModel

@Composable
fun ComplaintDetailsScreen(complaintId: String, complaintViewModel: ComplaintViewModel, onBack: () -> Unit) {
    val complaints by complaintViewModel.complaints.collectAsState()
    val complaint = complaints.find { it.id == complaintId } ?: complaints.firstOrNull()

    Scaffold(topBar = { AppTopBar(title = "Complaint Details", onMenuClick = onBack) }) { padding ->
        if (complaint != null) {
            Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Category: ${complaint.category}", style = MaterialTheme.typography.titleLarge)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Description: ${complaint.description}")
                        Text("Room: ${complaint.roomNo} • Student: ${complaint.studentName}")
                        Text("Date: ${complaint.date} • Priority: ${complaint.priority}")
                        Text("Status: ${complaint.status.name}")
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { complaintViewModel.updateStatus(complaint.id, ComplaintStatus.IN_PROGRESS) }) {
                        Text("Mark In Progress")
                    }
                    Button(onClick = { complaintViewModel.updateStatus(complaint.id, ComplaintStatus.RESOLVED) }) {
                        Text("Mark Resolved")
                    }
                }
            }
        }
    }
}
