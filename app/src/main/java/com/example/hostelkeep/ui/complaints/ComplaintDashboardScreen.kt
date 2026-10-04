package com.example.hostelkeep.ui.complaints

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.ComplaintCard
import com.example.hostelkeep.viewmodel.ComplaintViewModel

@Composable
fun ComplaintDashboardScreen(complaintViewModel: ComplaintViewModel, onNavigate: (String) -> Unit) {
    val complaints by complaintViewModel.complaints.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = "Complaints & Maintenance") },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigate("create_complaint") }) {
                Icon(Icons.Default.Add, contentDescription = "New Complaint")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(complaints) { complaint ->
                ComplaintCard(complaint = complaint, onClick = { onNavigate("complaint_details/${complaint.id}") })
            }
        }
    }
}
