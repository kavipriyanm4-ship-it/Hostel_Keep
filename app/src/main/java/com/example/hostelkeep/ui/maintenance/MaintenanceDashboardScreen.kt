package com.example.hostelkeep.ui.maintenance

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
import com.example.hostelkeep.viewmodel.MaintenanceViewModel

@Composable
fun MaintenanceDashboardScreen(maintenanceViewModel: MaintenanceViewModel, onNavigate: (String) -> Unit) {
    val list by maintenanceViewModel.maintenanceList.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = "Hostel Maintenance") },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigate("create_maintenance") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Maintenance")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(list) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(item.description)
                        Text("Location: ${item.location} • Status: ${item.status.name}")
                    }
                }
            }
        }
    }
}
