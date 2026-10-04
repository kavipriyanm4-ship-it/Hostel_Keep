package com.example.hostelkeep.ui.fees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.StatusChip
import com.example.hostelkeep.model.FeeStatus
import com.example.hostelkeep.viewmodel.FeeViewModel

@Composable
fun FeeDashboardScreen(feeViewModel: FeeViewModel, onBack: () -> Unit) {
    val fees by feeViewModel.fees.collectAsState()

    Scaffold(topBar = { AppTopBar(title = "Fee Dues & Payments", onMenuClick = onBack) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(fees) { fee ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(fee.title, style = MaterialTheme.typography.titleMedium)
                            StatusChip(
                                fee.status.name,
                                when (fee.status) {
                                    FeeStatus.PAID -> Color(16, 185, 129)
                                    FeeStatus.PENDING -> Color(245, 158, 11)
                                    FeeStatus.OVERDUE -> Color(239, 68, 68)
                                }
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Student: ${fee.studentName} (${fee.rollNo})")
                        Text("Amount: ₹${fee.amount} • Due: ${fee.dueDate}")
                        if (fee.receiptNo != null) {
                            Text("Receipt: ${fee.receiptNo} (Paid on ${fee.paidDate})", color = Color(16, 185, 129))
                        }
                        if (fee.status != FeeStatus.PAID) {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { feeViewModel.payFee(fee.id) }) {
                                Text("Pay Online Now")
                            }
                        }
                    }
                }
            }
        }
    }
}
