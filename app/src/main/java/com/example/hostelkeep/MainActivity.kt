package com.example.hostelkeep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.hostelkeep.navigation.AppNavigation
import com.example.hostelkeep.theme.HostelHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HostelHubTheme {
                AppNavigation()
            }
        }
    }
}
