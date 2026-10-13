package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpecForgeViewModel

data class NavItem(
    val screen: AppScreen,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun SpecForgeBottomNav(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val validationReport by viewModel.validationReport.collectAsState()
    val activeRequirements by viewModel.activeRequirements.collectAsState()

    val navItems = listOf(
        NavItem(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
        NavItem(AppScreen.PROJECTS, "Projects", Icons.Default.Folder, "nav_projects"),
        NavItem(AppScreen.SRS_EDITOR, "Editor", Icons.Default.Description, "nav_editor"),
        NavItem(AppScreen.REQUIREMENTS, "Reqs", Icons.Default.FactCheck, "nav_requirements"),
        NavItem(AppScreen.VALIDATION, "Validate", Icons.Default.Verified, "nav_validation"),
        NavItem(AppScreen.DOCUMENT_PREVIEW, "Preview", Icons.Default.Print, "nav_preview"),
        NavItem(AppScreen.TEMPLATES, "Templates", Icons.Default.Layers, "nav_templates"),
        NavItem(AppScreen.VERSION_HISTORY, "History", Icons.Default.History, "nav_history")
    )

    Surface(
        color = NavyDark,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxWidth()
        ) {
            navItems.forEach { item ->
                val selected = currentScreen == item.screen

                NavigationBarItem(
                    selected = selected,
                    onClick = { viewModel.navigateTo(item.screen) },
                    icon = {
                        if (item.screen == AppScreen.VALIDATION && validationReport != null && (validationReport?.errorCount ?: 0) > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White
                                    ) {
                                        Text("${validationReport?.errorCount}")
                                    }
                                }
                            ) {
                                Icon(item.icon, contentDescription = item.title)
                            }
                        } else if (item.screen == AppScreen.REQUIREMENTS && activeRequirements.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = ElectricBlue,
                                        contentColor = Color.White
                                    ) {
                                        Text("${activeRequirements.size}")
                                    }
                                }
                            ) {
                                Icon(item.icon, contentDescription = item.title)
                            }
                        } else {
                            Icon(item.icon, contentDescription = item.title)
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = ElectricBlueLight,
                        indicatorColor = ElectricBlue,
                        unselectedIconColor = SlateLight,
                        unselectedTextColor = SlateMuted
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
