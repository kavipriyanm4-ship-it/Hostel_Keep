package com.example.hostelkeep.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hostelkeep.model.UserRole
import com.example.hostelkeep.ui.admin.*
import com.example.hostelkeep.ui.attendance.*
import com.example.hostelkeep.ui.auth.*
import com.example.hostelkeep.ui.complaints.*
import com.example.hostelkeep.ui.emergency.*
import com.example.hostelkeep.ui.fees.*
import com.example.hostelkeep.ui.leave.*
import com.example.hostelkeep.ui.maintenance.*
import com.example.hostelkeep.ui.mess.*
import com.example.hostelkeep.ui.notifications.*
import com.example.hostelkeep.ui.outpass.*
import com.example.hostelkeep.ui.parent.*
import com.example.hostelkeep.ui.reports.*
import com.example.hostelkeep.ui.rooms.*
import com.example.hostelkeep.ui.security.*
import com.example.hostelkeep.ui.student.*
import com.example.hostelkeep.ui.students.*
import com.example.hostelkeep.ui.visitors.*
import com.example.hostelkeep.ui.warden.*
import com.example.hostelkeep.viewmodel.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GuardedRoute(
    authViewModel: AuthViewModel,
    route: String,
    navController: androidx.navigation.NavController,
    content: @Composable () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val role = currentUser?.role ?: UserRole.STUDENT

    // Restrict admin/warden management routes from students, security, and parents
    val isAdminOrWardenRoute = route.startsWith("admin") || route.startsWith("warden") ||
            route == Routes.STUDENTS_LIST || route == Routes.ADD_STUDENT || route == Routes.ROOM_ALLOCATION ||
            route == Routes.ADD_ROOM || route == Routes.ATTENDANCE_DASHBOARD || route == Routes.FEE_DASHBOARD ||
            route == Routes.REPORTS_SCREEN || route == Routes.ADMIN_REPORTS || route == Routes.ADMIN_SETTINGS ||
            route == Routes.WARDEN_ANNOUNCEMENTS

    val isUnauthorized = (role == UserRole.STUDENT || role == UserRole.SECURITY || role == UserRole.PARENT) && isAdminOrWardenRoute

    if (isUnauthorized) {
        val dashboardRoute = when (role) {
            UserRole.ADMIN -> Routes.ADMIN_DASHBOARD
            UserRole.WARDEN -> Routes.WARDEN_DASHBOARD
            UserRole.STUDENT -> Routes.STUDENT_DASHBOARD
            UserRole.PARENT -> Routes.PARENT_DASHBOARD
            UserRole.SECURITY -> Routes.SECURITY_DASHBOARD
        }
        LaunchedEffect(Unit) {
            navController.navigate(dashboardRoute) {
                popUpTo(dashboardRoute) { inclusive = true }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        content()
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val studentViewModel: StudentViewModel = viewModel()
    val roomViewModel: RoomViewModel = viewModel()
    val attendanceViewModel: AttendanceViewModel = viewModel()
    val leaveViewModel: LeaveViewModel = viewModel()
    val outpassViewModel: OutpassViewModel = viewModel()
    val feeViewModel: FeeViewModel = viewModel()
    val complaintViewModel: ComplaintViewModel = viewModel()
    val maintenanceViewModel: MaintenanceViewModel = viewModel()
    val messViewModel: MessViewModel = viewModel()
    val visitorViewModel: VisitorViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val emergencyViewModel: EmergencyViewModel = viewModel()

    val onLogout: () -> Unit = {
        FirebaseAuth.getInstance().signOut()
        navController.navigate(Routes.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onNavigateToDashboard = { role ->
                    val dest = when (role) {
                        UserRole.ADMIN -> Routes.ADMIN_DASHBOARD
                        UserRole.WARDEN -> Routes.WARDEN_DASHBOARD
                        UserRole.STUDENT -> Routes.STUDENT_DASHBOARD
                        UserRole.PARENT -> Routes.PARENT_DASHBOARD
                        UserRole.SECURITY -> Routes.SECURITY_DASHBOARD
                    }
                    navController.navigate(dest) { popUpTo(Routes.SPLASH) { inclusive = true } }
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { role ->
                    val dest = when (role) {
                        UserRole.ADMIN -> Routes.ADMIN_DASHBOARD
                        UserRole.WARDEN -> Routes.WARDEN_DASHBOARD
                        UserRole.STUDENT -> Routes.STUDENT_DASHBOARD
                        UserRole.PARENT -> Routes.PARENT_DASHBOARD
                        UserRole.SECURITY -> Routes.SECURITY_DASHBOARD
                    }
                    navController.navigate(dest) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                authViewModel = authViewModel,
                onRoleSelected = { role ->
                    val dest = when (role) {
                        UserRole.ADMIN -> Routes.ADMIN_DASHBOARD
                        UserRole.WARDEN -> Routes.WARDEN_DASHBOARD
                        UserRole.STUDENT -> Routes.STUDENT_DASHBOARD
                        UserRole.PARENT -> Routes.PARENT_DASHBOARD
                        UserRole.SECURITY -> Routes.SECURITY_DASHBOARD
                    }
                    navController.navigate(dest)
                }
            )
        }

        composable(Routes.ADMIN_DASHBOARD) {
            GuardedRoute(authViewModel, Routes.ADMIN_DASHBOARD, navController) {
                AdminDashboardScreen(studentViewModel, roomViewModel, complaintViewModel, feeViewModel, onNavigate = { navController.navigate(it) }, onLogout = onLogout)
            }
        }
        composable(Routes.WARDEN_DASHBOARD) {
            GuardedRoute(authViewModel, Routes.WARDEN_DASHBOARD, navController) {
                WardenDashboardScreen(onNavigate = { navController.navigate(it) }, onLogout = onLogout)
            }
        }
        composable(Routes.STUDENT_DASHBOARD) {
            StudentDashboardScreen(authViewModel, onNavigate = { navController.navigate(it) }, onLogout = onLogout)
        }
        composable(Routes.PARENT_DASHBOARD) {
            ParentDashboardScreen(onNavigate = { navController.navigate(it) })
        }
        composable(Routes.SECURITY_DASHBOARD) {
            SecurityDashboardScreen(onNavigate = { navController.navigate(it) }, onLogout = onLogout)
        }

        composable(Routes.ADMIN_REPORTS) {
            GuardedRoute(authViewModel, Routes.ADMIN_REPORTS, navController) {
                AdminReportsScreen(onBack = { navController.popBackStack() })
            }
        }
        composable(Routes.ADMIN_SETTINGS) {
            GuardedRoute(authViewModel, Routes.ADMIN_SETTINGS, navController) {
                AdminSettingsScreen(onBack = { navController.popBackStack() })
            }
        }
        composable(Routes.WARDEN_ANNOUNCEMENTS) {
            GuardedRoute(authViewModel, Routes.WARDEN_ANNOUNCEMENTS, navController) {
                WardenAnnouncementsScreen(notificationViewModel, onBack = { navController.popBackStack() })
            }
        }
        composable(Routes.STUDENT_PROFILE) { StudentProfileScreen(authViewModel, onBack = { navController.popBackStack() }) }
        composable(Routes.HOSTEL_RULES) { HostelRulesScreen(onBack = { navController.popBackStack() }) }

        composable(Routes.STUDENTS_LIST) {
            GuardedRoute(authViewModel, Routes.STUDENTS_LIST, navController) {
                StudentListScreen(studentViewModel, onNavigate = { navController.navigate(it) })
            }
        }
        composable(Routes.ADD_STUDENT) {
            GuardedRoute(authViewModel, Routes.ADD_STUDENT, navController) {
                AddStudentScreen(studentViewModel, onBack = { navController.popBackStack() })
            }
        }
        composable(
            route = Routes.STUDENT_DETAILS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            GuardedRoute(authViewModel, Routes.STUDENTS_LIST, navController) {
                StudentDetailsScreen(studentId, studentViewModel, onBack = { navController.popBackStack() })
            }
        }

        composable(Routes.ROOMS_LIST) { RoomListScreen(roomViewModel, onNavigate = { navController.navigate(it) }) }
        composable(Routes.ADD_ROOM) {
            GuardedRoute(authViewModel, Routes.ADD_ROOM, navController) {
                AddRoomScreen(roomViewModel, onBack = { navController.popBackStack() })
            }
        }
        composable(
            route = Routes.ROOM_DETAILS,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            RoomDetailsScreen(roomId, roomViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.ROOM_ALLOCATION) {
            GuardedRoute(authViewModel, Routes.ROOM_ALLOCATION, navController) {
                RoomAllocationScreen(roomViewModel, onBack = { navController.popBackStack() })
            }
        }

        composable(Routes.ATTENDANCE_DASHBOARD) {
            GuardedRoute(authViewModel, Routes.ATTENDANCE_DASHBOARD, navController) {
                AttendanceDashboardScreen(attendanceViewModel, authViewModel, onBack = { navController.popBackStack() })
            }
        }
        composable(Routes.STUDENT_ATTENDANCE) {
            GuardedRoute(authViewModel, Routes.STUDENT_ATTENDANCE, navController) {
                StudentAttendanceScreen(attendanceViewModel, authViewModel, onBack = { navController.popBackStack() })
            }
        }

        composable(Routes.LEAVE_DASHBOARD) {
            GuardedRoute(authViewModel, Routes.LEAVE_DASHBOARD, navController) {
                LeaveDashboardScreen(leaveViewModel, authViewModel, onNavigate = { navController.navigate(it) })
            }
        }
        composable(Routes.APPLY_LEAVE) {
            GuardedRoute(authViewModel, Routes.APPLY_LEAVE, navController) {
                ApplyLeaveScreen(leaveViewModel, authViewModel, onBack = { navController.popBackStack() })
            }
        }

        composable(Routes.OUTPASS_DASHBOARD) { OutpassDashboardScreen(currentUser, outpassViewModel, onNavigate = { navController.navigate(it) }) }
        composable(Routes.APPLY_OUTPASS) { ApplyOutpassScreen(currentUser, outpassViewModel, onBack = { navController.popBackStack() }) }
        composable(
            route = Routes.QR_OUTPASS,
            arguments = listOf(navArgument("outpassId") { type = NavType.StringType })
        ) { backStackEntry ->
            val outpassId = backStackEntry.arguments?.getString("outpassId") ?: ""
            QrOutpassScreen(outpassId, outpassViewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.FEE_DASHBOARD) {
            GuardedRoute(authViewModel, Routes.FEE_DASHBOARD, navController) {
                FeeDashboardScreen(feeViewModel, onBack = { navController.popBackStack() })
            }
        }
        composable(Routes.STUDENT_FEE) { StudentFeeScreen(feeViewModel, onBack = { navController.popBackStack() }) }

        composable(Routes.COMPLAINT_DASHBOARD) { ComplaintDashboardScreen(complaintViewModel, onNavigate = { navController.navigate(it) }) }
        composable(Routes.CREATE_COMPLAINT) { CreateComplaintScreen(complaintViewModel, onBack = { navController.popBackStack() }) }
        composable(
            route = Routes.COMPLAINT_DETAILS,
            arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId") ?: ""
            ComplaintDetailsScreen(complaintId, complaintViewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.MAINTENANCE_DASHBOARD) { MaintenanceDashboardScreen(maintenanceViewModel, onNavigate = { navController.navigate(it) }) }
        composable(Routes.CREATE_MAINTENANCE) { CreateMaintenanceScreen(maintenanceViewModel, onBack = { navController.popBackStack() }) }

        composable(Routes.MESS_DASHBOARD) { MessDashboardScreen(messViewModel, onNavigate = { navController.navigate(it) }) }
        composable(Routes.MESS_FEEDBACK) { MessFeedbackScreen(messViewModel, onBack = { navController.popBackStack() }) }

        composable(Routes.VISITOR_DASHBOARD) { VisitorDashboardScreen(visitorViewModel, onNavigate = { navController.navigate(it) }) }
        composable(Routes.REGISTER_VISITOR) { RegisterVisitorScreen(visitorViewModel, onBack = { navController.popBackStack() }) }
        composable(Routes.QR_SCANNER) { QrScannerScreen(currentUser, outpassViewModel, onBack = { navController.popBackStack() }) }
        composable(Routes.VISITOR_LOG) { VisitorLogScreen(visitorViewModel, onBack = { navController.popBackStack() }) }

        composable(Routes.NOTIFICATION_SCREEN) { NotificationScreen(notificationViewModel, onBack = { navController.popBackStack() }) }

        composable(Routes.EMERGENCY_SCREEN) { EmergencyScreen(emergencyViewModel, onBack = { navController.popBackStack() }) }
        composable(Routes.EMERGENCY_DASHBOARD) { EmergencyDashboardScreen(emergencyViewModel, onBack = { navController.popBackStack() }) }

        composable(Routes.REPORTS_SCREEN) {
            GuardedRoute(authViewModel, Routes.REPORTS_SCREEN, navController) {
                ReportsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
