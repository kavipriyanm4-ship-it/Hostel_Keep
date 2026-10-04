package com.example.hostelkeep.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar

@Composable
fun AdminSettingsScreen(onBack: () -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Hostel Configuration", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("System Settings & Parameters", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = "Block A, B, C", onValueChange = {}, label = { Text("Active Hostel Blocks") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = "21:30", onValueChange = {}, label = { Text("Night Curfew Time") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Button(onClick = {}) { Text("Save Changes") }
        }
    }
}
