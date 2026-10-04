package com.example.hostelkeep.ui.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.RoomCard
import com.example.hostelkeep.viewmodel.RoomViewModel

@Composable
fun RoomListScreen(roomViewModel: RoomViewModel, onNavigate: (String) -> Unit) {
    val rooms by roomViewModel.rooms.collectAsState()

    val totalRooms = rooms.size
    val availableRooms = rooms.count { it.status == "AVAILABLE" }
    val occupiedRooms = rooms.count { it.status == "OCCUPIED" || it.status == "PARTIALLY_OCCUPIED" }
    val fullRooms = rooms.count { it.status == "FULL" }

    Scaffold(
        topBar = { AppTopBar(title = "Rooms & Bed Allocation") },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigate("add_room") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Room")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard("Total", "$totalRooms", Modifier.weight(1f), MaterialTheme.colorScheme.primary)
                MetricCard("Available", "$availableRooms", Modifier.weight(1f), Color(16, 185, 129))
                MetricCard("Occupied", "$occupiedRooms", Modifier.weight(1f), Color(245, 158, 11))
                MetricCard("Full", "$fullRooms", Modifier.weight(1f), Color(239, 68, 68))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(rooms) { room ->
                    RoomCard(room = room, onClick = { onNavigate("room_details/${room.id}") })
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
