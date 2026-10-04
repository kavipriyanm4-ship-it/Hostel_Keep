package com.example.hostelkeep.ui.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.model.Room
import com.example.hostelkeep.viewmodel.RoomViewModel

@Composable
fun AddRoomScreen(
    roomViewModel: RoomViewModel,
    onBack: () -> Unit
) {
    var hostelName by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var floorNumber by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }
    var roomType by remember { mutableStateOf("SINGLE") }
    var capacityText by remember { mutableStateOf("") }
    var numBedsText by remember { mutableStateOf("") }

    val errorMessage by roomViewModel.errorMessage.collectAsState()
    val successMessage by roomViewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            roomViewModel.clearMessages()
            onBack()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Add New Room", onMenuClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            AppTextField(value = hostelName, onValueChange = { hostelName = it }, label = "Hostel Name")
            AppTextField(value = building, onValueChange = { building = it }, label = "Building / Block")
            AppTextField(value = floorNumber, onValueChange = { floorNumber = it }, label = "Floor Number")
            AppTextField(value = roomNumber, onValueChange = { roomNumber = it }, label = "Room Number (e.g. 104)")

            Text("Room Type", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("SINGLE", "DOUBLE", "TRIPLE", "FOUR_BED").forEach { type ->
                    FilterChip(
                        selected = roomType == type,
                        onClick = { roomType = type },
                        label = { Text(type) }
                    )
                }
            }

            AppTextField(value = capacityText, onValueChange = { capacityText = it }, label = "Capacity")
            AppTextField(value = numBedsText, onValueChange = { numBedsText = it }, label = "Number of Beds")

            Spacer(Modifier.height(16.dp))

            AppButton(
                text = "Create Room & Beds",
                onClick = {
                    val cap = capacityText.toIntOrNull() ?: 2
                    val bedsCount = numBedsText.toIntOrNull() ?: cap
                    val newRoom = Room(
                        id = "room_${System.currentTimeMillis()}",
                        roomNo = roomNumber,
                        roomNumber = roomNumber,
                        block = building,
                        hostelId = hostelName,
                        capacity = cap,
                        type = roomType,
                        status = "AVAILABLE"
                    )
                    roomViewModel.addRoom(newRoom, bedsCount)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
