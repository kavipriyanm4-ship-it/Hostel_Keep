package com.example.hostelkeep.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar

@Composable
fun AdminReportsScreen(onBack: () -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Admin Reports", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Financial & Occupancy Summary Report", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Revenue Collected: ₹1,35,000")
                    Spacer(Modifier.height(8.dp))
                    Text("Total Dues Pending: ₹90,000")
                    Spacer(Modifier.height(8.dp))
                    Text("Mess Rating Average: 4.3 / 5.0")
                    Spacer(Modifier.height(8.dp))
                    Text("Attendance Compliance: 94.5%")
                }
            }
        }
    }
}
