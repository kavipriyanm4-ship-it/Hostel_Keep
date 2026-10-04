package com.example.hostelkeep.ui.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.RoomViewModel

@Composable
fun RoomAllocationScreen(roomViewModel: RoomViewModel, onBack: () -> Unit) {
    var roomId by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }

    Scaffold(topBar = { AppTopBar(title = "Room Allocation", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(value = roomId, onValueChange = { roomId = it }, label = "Room ID (r1, r2, r4...)")
            AppTextField(value = studentName, onValueChange = { studentName = it }, label = "Student Name")
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Allocate Bed",
                onClick = {
                    roomViewModel.allocateRoom(roomId, studentName)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
