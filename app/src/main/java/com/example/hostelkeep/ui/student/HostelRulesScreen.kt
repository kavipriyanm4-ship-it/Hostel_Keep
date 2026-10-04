package com.example.hostelkeep.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar

@Composable
fun HostelRulesScreen(onBack: () -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Hostel Rules & Regulations", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("1. Night Curfew", style = MaterialTheme.typography.titleMedium)
            Text("All students must return to their respective hostel rooms by 9:30 PM on weekdays and 10:30 PM on weekends.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Text("2. Outpass Mandate", style = MaterialTheme.typography.titleMedium)
            Text("Leaving campus requires an approved outpass with QR verification at the main security gate.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Text("3. Visitor Policy", style = MaterialTheme.typography.titleMedium)
            Text("Visitors are allowed only in the designated visitor lounge during visiting hours (4 PM to 7 PM).", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
