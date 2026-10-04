package com.example.hostelkeep.ui.students

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.SearchBar
import com.example.hostelkeep.components.StudentCard
import com.example.hostelkeep.viewmodel.StudentViewModel

@Composable
fun StudentListScreen(studentViewModel: StudentViewModel, onNavigate: (String) -> Unit) {
    val students by studentViewModel.students.collectAsState()
    val query by studentViewModel.searchQuery.collectAsState()

    val filtered = students.filter { it.name.contains(query, true) || it.rollNo.contains(query, true) || it.department.contains(query, true) }

    Scaffold(
        topBar = { AppTopBar(title = "Students Directory") },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigate("add_student") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            SearchBar(query = query, onQueryChange = studentViewModel::setSearchQuery, placeholder = "Search by name, roll no, dept...")
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(filtered) { student ->
                    StudentCard(student = student, onClick = { onNavigate("student_details/${student.id}") })
                }
            }
        }
    }
}
