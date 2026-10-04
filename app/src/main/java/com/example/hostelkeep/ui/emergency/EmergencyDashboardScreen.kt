package com.example.hostelkeep.ui.emergency

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.StatusChip
import com.example.hostelkeep.viewmodel.EmergencyViewModel

@Composable
fun EmergencyDashboardScreen(emergencyViewModel: EmergencyViewModel, onBack: () -> Unit) {
    val list by emergencyViewModel.emergencies.collectAsState()

    Scaffold(topBar = { AppTopBar(title = "Emergency Alerts Dashboard", onMenuClick = onBack) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(list) { em ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(em.studentName, style = MaterialTheme.typography.titleMedium)
                            StatusChip(em.status, if (em.status == "Active") Color.Red else Color(16, 185, 129))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Roll: ${em.rollNo} • Room: ${em.roomNo}")
                        Text("Type: ${em.emergencyType} • Time: ${em.time}")
                        if (em.status == "Active") {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { emergencyViewModel.resolveEmergency(em.id) }) {
                                Text("Mark Resolved")
                            }
                        }
                    }
                }
            }
        }
    }
}
