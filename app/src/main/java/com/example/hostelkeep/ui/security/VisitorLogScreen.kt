package com.example.hostelkeep.ui.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.StatusChip
import com.example.hostelkeep.viewmodel.VisitorViewModel

@Composable
fun VisitorLogScreen(visitorViewModel: VisitorViewModel, onBack: () -> Unit) {
    val visitors by visitorViewModel.visitors.collectAsState()

    Scaffold(topBar = { AppTopBar(title = "Visitor Logbook", onMenuClick = onBack) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(visitors) { visitor ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(visitor.visitorName, style = MaterialTheme.typography.titleMedium)
                            StatusChip(visitor.passNo, MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Visiting: ${visitor.studentNameToVisit} (${visitor.relation})")
                        Text("Purpose: ${visitor.purpose}")
                        Text("Entry: ${visitor.entryTime} • Exit: ${visitor.exitTime ?: "Inside Campus"}")
                        if (visitor.exitTime == null) {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { visitorViewModel.checkoutVisitor(visitor.id) }) {
                                Text("Check Out")
                            }
                        }
                    }
                }
            }
        }
    }
}
