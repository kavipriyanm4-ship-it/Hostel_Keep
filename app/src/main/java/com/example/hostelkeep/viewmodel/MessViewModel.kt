package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.MessRepository
import com.example.hostelkeep.model.MessMenu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessViewModel(
    private val messRepository: MessRepository = MessRepository()
) : ViewModel() {
    private val _messMenu = MutableStateFlow<List<MessMenu>>(emptyList())
    val messMenu: StateFlow<List<MessMenu>> = _messMenu.asStateFlow()

    init {
        viewModelScope.launch {
            messRepository.getMessMenuFlow().collect { list ->
                _messMenu.value = list
            }
        }
    }

    fun submitFeedback(day: String, rating: Float, comment: String) {
        viewModelScope.launch {
            messRepository.submitFeedback(day, rating, comment)
        }
    }
}
