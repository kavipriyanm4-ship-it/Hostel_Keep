package com.example.hostelkeep.ui.mess

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.MessViewModel

@Composable
fun MessDashboardScreen(messViewModel: MessViewModel, onNavigate: (String) -> Unit) {
    val menuList by messViewModel.messMenu.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = "Mess Menu & Rating") },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigate("mess_feedback") }) {
                Icon(Icons.Default.Star, contentDescription = "Feedback")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(menuList) { menu ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(menu.dayOfWeek, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("★ ${String.format("%.1f", menu.rating)} (${menu.totalReviews})", color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Breakfast: ${menu.breakfast}", style = MaterialTheme.typography.bodyMedium)
                        Text("Lunch: ${menu.lunch}", style = MaterialTheme.typography.bodyMedium)
                        Text("Snacks: ${menu.snacks}", style = MaterialTheme.typography.bodyMedium)
                        Text("Dinner: ${menu.dinner}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
