package com.example.hostelkeep.ui.visitors

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.DashboardCard
import com.example.hostelkeep.viewmodel.VisitorViewModel

@Composable
fun VisitorDashboardScreen(visitorViewModel: VisitorViewModel, onNavigate: (String) -> Unit) {
    Scaffold(topBar = { AppTopBar(title = "Visitor Management", onMenuClick = { onNavigate("security_dashboard") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCard(title = "Register Visitor", value = "Issue entry pass", icon = Icons.Default.PersonAdd, color = MaterialTheme.colorScheme.primary, onClick = { onNavigate("register_visitor") })
            DashboardCard(title = "Active Visitors Log", value = "View entries & exits", icon = Icons.Default.FormatListBulleted, color = MaterialTheme.colorScheme.secondary, onClick = { onNavigate("visitor_log") })
        }
    }
}
