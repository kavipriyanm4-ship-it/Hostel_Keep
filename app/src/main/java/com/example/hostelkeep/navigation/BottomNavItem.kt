package com.example.hostelkeep.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem(Routes.STUDENT_DASHBOARD, "Home", Icons.Default.Home)
    object Attendance : BottomNavItem(Routes.STUDENT_ATTENDANCE, "Attendance", Icons.Default.CheckCircle)
    object Leave : BottomNavItem(Routes.LEAVE_DASHBOARD, "Leave", Icons.Default.EventNote)
    object Mess : BottomNavItem(Routes.MESS_DASHBOARD, "Mess", Icons.Default.Restaurant)
    object Profile : BottomNavItem(Routes.STUDENT_PROFILE, "Profile", Icons.Default.Person)

    object AdminHome : BottomNavItem(Routes.ADMIN_DASHBOARD, "Home", Icons.Default.Home)
    object AdminStudents : BottomNavItem(Routes.STUDENTS_LIST, "Students", Icons.Default.People)
    object AdminRooms : BottomNavItem(Routes.ROOMS_LIST, "Rooms", Icons.Default.MeetingRoom)
    object AdminReports : BottomNavItem(Routes.REPORTS_SCREEN, "Reports", Icons.Default.BarChart)

    object SecurityHome : BottomNavItem(Routes.SECURITY_DASHBOARD, "Home", Icons.Default.Security)
    object SecurityScanner : BottomNavItem(Routes.QR_SCANNER, "Scanner", Icons.Default.QrCodeScanner)
    object VisitorLog : BottomNavItem(Routes.VISITOR_LOG, "Visitors", Icons.Default.List)
}
