package com.example.hostelkeep.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hostelkeep.model.Complaint
import com.example.hostelkeep.model.Notification
import com.example.hostelkeep.model.Room
import com.example.hostelkeep.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onMenuClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            }
        },
        actions = {
            if (onNotificationClick != null) {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun AppBottomBar(
    items: List<Triple<String, String, ImageVector>>,
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp) },
                selected = selected,
                onClick = { onItemClick(route) }
            )
        }
    }
}

@Composable
fun AppDrawer(
    currentRole: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("HostelHub Manager", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(4.dp))
                Text("Role: $currentRole", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            }
        }
        Divider(Modifier.padding(vertical = 8.dp))
        NavigationDrawerItem(
            label = { Text("Dashboard") },
            selected = false,
            icon = { Icon(Icons.Default.Home, null) },
            onClick = { onNavigate("dashboard"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Students Directory") },
            selected = false,
            icon = { Icon(Icons.Default.People, null) },
            onClick = { onNavigate("students_list"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Rooms & Beds") },
            selected = false,
            icon = { Icon(Icons.Default.MeetingRoom, null) },
            onClick = { onNavigate("rooms_list"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Attendance") },
            selected = false,
            icon = { Icon(Icons.Default.CheckCircle, null) },
            onClick = { onNavigate("attendance_dashboard"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Leave Requests") },
            selected = false,
            icon = { Icon(Icons.Default.EventNote, null) },
            onClick = { onNavigate("leave_dashboard"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Outpass & Security") },
            selected = false,
            icon = { Icon(Icons.Default.QrCode, null) },
            onClick = { onNavigate("outpass_dashboard"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Fee Payments") },
            selected = false,
            icon = { Icon(Icons.Default.Payment, null) },
            onClick = { onNavigate("fee_dashboard"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Complaints & Maintenance") },
            selected = false,
            icon = { Icon(Icons.Default.Build, null) },
            onClick = { onNavigate("complaint_dashboard"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Mess Menu & Feedback") },
            selected = false,
            icon = { Icon(Icons.Default.Restaurant, null) },
            onClick = { onNavigate("mess_dashboard"); onClose() }
        )
        NavigationDrawerItem(
            label = { Text("Emergency SOS") },
            selected = false,
            icon = { Icon(Icons.Default.Warning, null) },
            onClick = { onNavigate("emergency_dashboard"); onClose() }
        )
        Spacer(Modifier.weight(1f))
        Divider()
        NavigationDrawerItem(
            label = { Text("Logout", color = MaterialTheme.colorScheme.error) },
            selected = false,
            icon = { Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error) },
            onClick = { onLogout(); onClose() }
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = color)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun StatusChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        singleLine = true
    )
}

@Composable
fun EmptyState(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Text("Oops, something went wrong", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun AppButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ProfileHeader(name: String, subtitle: String, email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(2).uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Text(email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String? = null, onActionClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(actionText, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun StudentCard(student: Student, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(student.name.take(1), color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Roll: ${student.rollNo} • Dept: ${student.department}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Room: ${student.roomNo} (${student.bedNo})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            StatusChip(student.status, if (student.status == "Active") Color(16, 185, 129) else Color.Red)
        }
    }
}

@Composable
fun RoomCard(room: Room, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Room ${room.roomNo} (${room.block})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusChip(room.type, MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.height(8.dp))
            Text("Capacity: ${room.occupied}/${room.capacity} Occupied", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (room.occupants.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text("Occupants: ${room.occupants.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ComplaintCard(complaint: Complaint, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(complaint.category, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusChip(
                    complaint.status.name,
                    when (complaint.status) {
                        com.example.hostelkeep.model.ComplaintStatus.OPEN -> Color(239, 68, 68)
                        com.example.hostelkeep.model.ComplaintStatus.IN_PROGRESS -> Color(245, 158, 11)
                        com.example.hostelkeep.model.ComplaintStatus.RESOLVED -> Color(16, 185, 129)
                    }
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(complaint.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Room: ${complaint.roomNo} • By: ${complaint.studentName}", style = MaterialTheme.typography.bodySmall)
                Text(complaint.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun NotificationCard(notification: Notification, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(notification.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusChip(notification.category, MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(4.dp))
            Text(notification.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(notification.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text("Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
