package com.example.hostelkeep.ui.fees

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.FeeViewModel

@Composable
fun StudentFeeScreen(feeViewModel: FeeViewModel, onBack: () -> Unit) {
    FeeDashboardScreen(feeViewModel = feeViewModel, onBack = onBack)
}
