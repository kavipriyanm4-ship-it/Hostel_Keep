package com.example.hostelkeep.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar

@Composable
fun ReportsScreen(onBack: () -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Analytics & Reports", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Hostel Management Analytics", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Capacity: 200 Beds")
                    Text("Currently Occupied: 180 Beds (90%)")
                    Text("Monthly Mess Expense: ₹4,50,000")
                    Text("Pending Maintenance Tasks: 3")
                }
            }
        }
    }
}
