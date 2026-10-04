package com.example.hostelkeep.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.NotificationCard
import com.example.hostelkeep.viewmodel.NotificationViewModel

@Composable
fun NotificationScreen(notificationViewModel: NotificationViewModel, onBack: () -> Unit) {
    val notifications by notificationViewModel.notifications.collectAsState()

    Scaffold(topBar = { AppTopBar(title = "Announcements & Notifications", onMenuClick = onBack) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(notifications) { notification ->
                NotificationCard(notification = notification, onClick = { notificationViewModel.markAsRead(notification.id) })
            }
        }
    }
}
