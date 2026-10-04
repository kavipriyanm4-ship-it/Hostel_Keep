package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.FeeRepository
import com.example.hostelkeep.model.Fee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeeViewModel(
    private val feeRepository: FeeRepository = FeeRepository()
) : ViewModel() {
    private val _fees = MutableStateFlow<List<Fee>>(emptyList())
    val fees: StateFlow<List<Fee>> = _fees.asStateFlow()

    init {
        viewModelScope.launch {
            feeRepository.getFeesFlow().collect { list ->
                _fees.value = list
            }
        }
    }

    fun payFee(feeId: String) {
        viewModelScope.launch {
            feeRepository.payFee(feeId)
        }
    }
}
