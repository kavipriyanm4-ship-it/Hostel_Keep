package com.example.hostelkeep.ui.outpass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.OutpassViewModel

@Composable
fun QrOutpassScreen(outpassId: String, outpassViewModel: OutpassViewModel, onBack: () -> Unit) {
    val outpasses by outpassViewModel.outpasses.collectAsState()
    val outpass = outpasses.find { it.id == outpassId } ?: outpasses.firstOrNull()

    Scaffold(topBar = { AppTopBar(title = "Gate Outpass QR", onMenuClick = onBack) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (outpass != null) {
                Card(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(outpass.studentName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Roll: ${outpass.rollNo}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.LightGray, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(150.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Destination: ${outpass.destination}")
                        Text("Valid Out: ${outpass.outTime} | In: ${outpass.inTime}")
                        Spacer(Modifier.height(16.dp))
                        Text(outpass.qrCodeData, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
