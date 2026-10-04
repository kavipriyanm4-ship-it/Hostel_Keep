package com.example.hostelkeep.ui.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.StatusChip
import com.example.hostelkeep.model.Bed
import com.example.hostelkeep.viewmodel.RoomViewModel

@Composable
fun RoomDetailsScreen(
    roomId: String,
    roomViewModel: RoomViewModel,
    onBack: () -> Unit
) {
    val rooms by roomViewModel.rooms.collectAsState()
    val room = rooms.find { id -> id.id == roomId } ?: roomViewModel.selectedRoom.collectAsState().value
    val beds by roomViewModel.beds.collectAsState()
    val errorMessage by roomViewModel.errorMessage.collectAsState()
    val successMessage by roomViewModel.successMessage.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newCapacityText by remember { mutableStateOf("${room?.capacity ?: 2}") }

    val isAdmin = true

    LaunchedEffect(roomId) {
        roomViewModel.loadRoomDetails(roomId)
        roomViewModel.loadBeds(roomId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Room ${room?.roomNo ?: room?.roomNumber ?: roomId}",
                onMenuClick = onBack,
                actions = {
                    if (room != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Room")
                        }
                        if (isAdmin) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Room", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (room == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (errorMessage != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(errorMessage!!, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
                if (successMessage != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Text(successMessage!!, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Room ${room.roomNo.ifEmpty { room.roomNumber }} (${room.block})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            StatusChip(
                                room.status,
                                when (room.status) {
                                    "AVAILABLE" -> Color(16, 185, 129)
                                    "PARTIALLY_OCCUPIED" -> Color(245, 158, 11)
                                    "FULL", "OCCUPIED" -> Color(239, 68, 68)
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                        Divider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Hostel: ${room.hostelId.ifEmpty { "Kaveri Hostel" }}")
                            Text("Type: ${room.type}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Capacity: ${room.capacity}")
                            Text("Occupied Beds: ${room.occupiedBeds.takeIf { it > 0 } ?: room.occupied}")
                        }
                    }
                }

                Text("Beds & Occupants", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(beds) { bed ->
                        BedCard(bed = bed)
                    }
                }
            }
        }
    }

    if (showEditDialog && room != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update Room Capacity") },
            text = {
                Column {
                    Text("Current Capacity: ${room.capacity}")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCapacityText,
                        onValueChange = { newCapacityText = it },
                        label = { Text("New Capacity") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    val cap = newCapacityText.toIntOrNull() ?: room.capacity
                    roomViewModel.updateRoom(room, cap)
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog && room != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Room") },
            text = { Text("Are you sure you want to delete room ${room.roomNo}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        roomViewModel.deleteRoom(room.id, isAdmin)
                        onBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BedCard(bed: Bed) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Bed ${bed.bedNo.ifEmpty { bed.bedNumber }}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (!bed.studentId.isNullOrEmpty()) {
                    Text("Assigned Student ID: ${bed.studentId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                } else {
                    Text("No student assigned", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            StatusChip(
                bed.status,
                if (bed.status == "AVAILABLE") Color(16, 185, 129) else Color(239, 68, 68)
            )
        }
    }
}
