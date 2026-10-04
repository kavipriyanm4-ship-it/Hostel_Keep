package com.example.hostelkeep.ui.warden

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.NotificationViewModel

@Composable
fun WardenAnnouncementsScreen(notificationViewModel: NotificationViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Scaffold(topBar = { AppTopBar(title = "New Announcement", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            AppTextField(value = title, onValueChange = { title = it }, label = "Announcement Title")
            Spacer(Modifier.height(16.dp))
            AppTextField(value = message, onValueChange = { message = it }, label = "Message Content")
            Spacer(Modifier.height(24.dp))
            AppButton(
                text = "Broadcast to All Students",
                onClick = {
                    if (title.isNotEmpty() && message.isNotEmpty()) {
                        notificationViewModel.addNotification(title, message, category)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
