package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpecForgeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecForgeTopBar(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    val activeProject by viewModel.activeProject.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val lastSaveTime by viewModel.lastSaveTime.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Surface(
        color = NavyDark,
        tonalElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Brand Logo & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "SpecForge Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SPEC",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 17.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "FORGE",
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlueLight,
                            fontSize = 17.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "SRS PLATFORM",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateMuted,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Active Project Badge
            if (activeProject != null) {
                Surface(
                    color = NavySurface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ElectricBlueLight)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "[${activeProject?.keyPrefix}] ${activeProject?.title}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(1f))

            // DataStore Offline & Sync Status Pill
            val isOnline by viewModel.isOnline.collectAsState()
            val isSyncing by viewModel.isSyncing.collectAsState()
            val pendingCount by viewModel.pendingMutationsCount.collectAsState()

            Surface(
                color = if (!isOnline) Color(0xFFF59E0B).copy(alpha = 0.2f) else NavySurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (!isOnline) Color(0xFFF59E0B).copy(alpha = 0.5f) else SlateBorder
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        if (!isOnline) {
                            viewModel.toggleSimulateOffline()
                        } else {
                            viewModel.syncNow()
                        }
                    }
                    .testTag("topbar_sync_pill")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(12.dp),
                            color = ElectricBlueLight
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Syncing...",
                            fontSize = 10.sp,
                            color = ElectricBlueLight,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (!isOnline) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (pendingCount > 0) "Offline (+$pendingCount)" else "Offline Cache",
                            fontSize = 10.sp,
                            color = Color(0xFFF59E0B),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "DataStore Sync",
                            fontSize = 10.sp,
                            color = SlateLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Autosave indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(14.dp),
                        color = ElectricBlueLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saving...",
                        fontSize = 11.sp,
                        color = ElectricBlueLight
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Saved",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Saved",
                        fontSize = 11.sp,
                        color = SlateLight
                    )
                }
            }

            // Quick Export action
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.DOCUMENT_PREVIEW) },
                modifier = Modifier.testTag("topbar_export_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "Export Document",
                    tint = ElectricBlueLight
                )
            }

            // User Profile avatar
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.PROFILE_SETTINGS) },
                modifier = Modifier.testTag("topbar_profile_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentUser?.fullName?.contains("Evans") == true) "EA" else currentUser?.fullName?.take(1)?.uppercase() ?: "EA",
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlueLight,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
