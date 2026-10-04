package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.VisitorRepository
import com.example.hostelkeep.model.Visitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class VisitorViewModel(
    private val visitorRepository: VisitorRepository = VisitorRepository()
) : ViewModel() {
    private val _visitors = MutableStateFlow<List<Visitor>>(emptyList())
    val visitors: StateFlow<List<Visitor>> = _visitors.asStateFlow()

    init {
        viewModelScope.launch {
            visitorRepository.getVisitorsFlow().collect { list ->
                _visitors.value = list
            }
        }
    }

    fun registerVisitor(name: String, phone: String, studentName: String, relation: String, purpose: String) {
        viewModelScope.launch {
            val timeDf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val newV = Visitor(
                id = "v_${System.currentTimeMillis()}",
                visitorName = name,
                phone = phone,
                studentNameToVisit = studentName,
                relation = relation,
                entryTime = timeDf.format(Date()),
                exitTime = null,
                purpose = purpose,
                passNo = "PASS-${(100..999).random()}"
            )
            visitorRepository.registerVisitor(newV)
        }
    }

    fun checkoutVisitor(visitorId: String) {
        viewModelScope.launch {
            visitorRepository.checkoutVisitor(visitorId)
        }
    }
}
