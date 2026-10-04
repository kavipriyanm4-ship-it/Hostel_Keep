package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.NotificationRepository
import com.example.hostelkeep.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NotificationViewModel(
    private val notificationRepository: NotificationRepository = NotificationRepository()
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    init {
        viewModelScope.launch {
            notificationRepository.getNotificationsFlow().collect { list ->
                _notifications.value = list
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun addNotification(title: String, message: String, category: String) {
        viewModelScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val newN = Notification(
                id = "n_${System.currentTimeMillis()}",
                title = title,
                message = message,
                date = df.format(Date()),
                category = category,
                isRead = false
            )
            notificationRepository.addNotification(newN)
        }
    }
}
