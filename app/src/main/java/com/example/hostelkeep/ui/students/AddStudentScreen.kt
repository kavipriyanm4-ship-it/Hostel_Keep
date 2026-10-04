package com.example.hostelkeep.ui.students

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.model.Student
import com.example.hostelkeep.viewmodel.StudentViewModel

@Composable
fun AddStudentScreen(studentViewModel: StudentViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("") }
    var roomNo by remember { mutableStateOf("") }
    var bedNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }

    Scaffold(topBar = { AppTopBar(title = "Add New Student", onMenuClick = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppTextField(value = name, onValueChange = { name = it }, label = "Full Name")
            AppTextField(value = rollNo, onValueChange = { rollNo = it }, label = "Roll Number")
            AppTextField(value = dept, onValueChange = { dept = it }, label = "Department (CSE/ECE/...)")
            AppTextField(value = roomNo, onValueChange = { roomNo = it }, label = "Room Number")
            AppTextField(value = bedNo, onValueChange = { bedNo = it }, label = "Bed Number (Bed 1/Bed 2)")
            AppTextField(value = email, onValueChange = { email = it }, label = "Email")
            AppTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number")
            AppTextField(value = parentName, onValueChange = { parentName = it }, label = "Parent Name")
            AppTextField(value = parentPhone, onValueChange = { parentPhone = it }, label = "Parent Phone")
            AppTextField(value = bloodGroup, onValueChange = { bloodGroup = it }, label = "Blood Group")
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Save Student",
                onClick = {
                    if (name.isNotEmpty() && rollNo.isNotEmpty()) {
                        val s = Student(
                            id = "s_${System.currentTimeMillis()}",
                            rollNo = rollNo,
                            name = name,
                            department = dept,
                            year = 3,
                            roomNo = roomNo,
                            bedNo = bedNo,
                            email = email,
                            phone = phone,
                            parentName = parentName,
                            parentPhone = parentPhone,
                            bloodGroup = bloodGroup
                        )
                        studentViewModel.addStudent(s)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
