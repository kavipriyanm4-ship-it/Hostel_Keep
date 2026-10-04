package com.example.hostelkeep.ui.mess

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.MessViewModel

@Composable
fun MessFeedbackScreen(messViewModel: MessViewModel, onBack: () -> Unit) {
    var day by remember { mutableStateOf("") }
    var ratingStr by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    Scaffold(topBar = { AppTopBar(title = "Submit Mess Feedback", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(value = day, onValueChange = { day = it }, label = "Day of Week (Monday, Tuesday...)")
            AppTextField(value = ratingStr, onValueChange = { ratingStr = it }, label = "Rating (1.0 to 5.0)")
            AppTextField(value = comment, onValueChange = { comment = it }, label = "Comments")
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Submit Rating",
                onClick = {
                    val rating = ratingStr.toFloatOrNull() ?: 4.0f
                    messViewModel.submitFeedback(day, rating, comment)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
