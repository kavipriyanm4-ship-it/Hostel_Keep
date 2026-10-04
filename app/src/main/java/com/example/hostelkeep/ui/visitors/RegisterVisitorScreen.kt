package com.example.hostelkeep.ui.visitors

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.VisitorViewModel

@Composable
fun RegisterVisitorScreen(visitorViewModel: VisitorViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }

    Scaffold(topBar = { AppTopBar(title = "Register Visitor", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(value = name, onValueChange = { name = it }, label = "Visitor Full Name")
            AppTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number")
            AppTextField(value = studentName, onValueChange = { studentName = it }, label = "Student Name to Visit")
            AppTextField(value = relation, onValueChange = { relation = it }, label = "Relation")
            AppTextField(value = purpose, onValueChange = { purpose = it }, label = "Purpose of Visit")
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Generate Pass & Register",
                onClick = {
                    if (name.isNotEmpty()) {
                        visitorViewModel.registerVisitor(name, phone, studentName, relation, purpose)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
