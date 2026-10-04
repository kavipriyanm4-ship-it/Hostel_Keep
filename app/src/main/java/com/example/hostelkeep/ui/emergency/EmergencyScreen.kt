package com.example.hostelkeep.ui.emergency

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.EmergencyViewModel

@Composable
fun EmergencyScreen(emergencyViewModel: EmergencyViewModel, onBack: () -> Unit) {
    var triggered by remember { mutableStateOf(false) }

    Scaffold(topBar = { AppTopBar(title = "Emergency SOS", onMenuClick = onBack) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(96.dp))
            Spacer(Modifier.height(16.dp))
            Text("Emergency SOS Alert", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Text("Pressing this button immediately alerts the hostel warden, security guards, and campus admin with your room location.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(32.dp))
            AppButton(
                text = if (triggered) "SOS Alert Sent!" else "TRIGGER EMERGENCY SOS",
                onClick = {
                    emergencyViewModel.triggerSOS("Arun Kumar", "CS2101", "101", "Medical / Urgent")
                    triggered = true
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
