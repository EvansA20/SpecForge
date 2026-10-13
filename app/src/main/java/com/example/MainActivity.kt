package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SpecForgeBottomNav
import com.example.ui.components.SpecForgeTopBar
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CreateProjectDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentPreviewScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.RequirementsScreen
import com.example.ui.screens.SrsEditorScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.ValidationScreen
import com.example.ui.screens.VersionHistoryScreen
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDarkest
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpecForgeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SpecForgeApp()
            }
        }
    }
}

@Composable
fun SpecForgeApp(
    viewModel: SpecForgeViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val pendingMutations by viewModel.pendingMutationsCount.collectAsState()
    val snackMessage by viewModel.snackMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCreateProjectDialog by remember { mutableStateOf(false) }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackMessage()
        }
    }

    // Hardware back button navigation handling
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.AUTH) {
        viewModel.navigateBack()
    }

    if (currentScreen == AppScreen.AUTH || currentUser == null) {
        AuthScreen(viewModel = viewModel)
    } else {
        Scaffold(
            topBar = {
                SpecForgeTopBar(viewModel = viewModel)
            },
            bottomBar = {
                SpecForgeBottomNav(viewModel = viewModel)
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = NavyDarkest,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(NavyDarkest)
            ) {
                // Offline Notice Strip
                if (!isOnline) {
                    Surface(
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF59E0B))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (pendingMutations > 0)
                                        "Offline Mode: $pendingMutations changes queued in DataStore"
                                    else
                                        "Offline Mode: Viewing DataStore cached SRS documents",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                            TextButton(
                                onClick = { viewModel.toggleSimulateOffline() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Reconnect & Sync", fontSize = 11.sp, color = ElectricBlueLight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (currentScreen) {
                        AppScreen.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            onOpenCreateProjectDialog = { showCreateProjectDialog = true }
                        )
                        AppScreen.PROJECTS -> ProjectsScreen(
                            viewModel = viewModel
                        )
                        AppScreen.SRS_EDITOR -> SrsEditorScreen(
                            viewModel = viewModel
                        )
                        AppScreen.REQUIREMENTS -> RequirementsScreen(
                            viewModel = viewModel
                        )
                        AppScreen.VALIDATION -> ValidationScreen(
                            viewModel = viewModel
                        )
                        AppScreen.DOCUMENT_PREVIEW -> DocumentPreviewScreen(
                            viewModel = viewModel
                        )
                        AppScreen.TEMPLATES -> TemplatesScreen(
                            viewModel = viewModel
                        )
                        AppScreen.VERSION_HISTORY -> VersionHistoryScreen(
                            viewModel = viewModel
                        )
                        AppScreen.PROFILE_SETTINGS -> ProfileSettingsScreen(
                            viewModel = viewModel
                        )
                        AppScreen.AUTH -> AuthScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }

            if (showCreateProjectDialog) {
                CreateProjectDialog(
                    onDismiss = { showCreateProjectDialog = false },
                    onCreate = { title, prefix, desc, cat, audience, stack ->
                        viewModel.createProject(title, prefix, desc, cat, audience, stack)
                        showCreateProjectDialog = false
                    }
                )
            }
        }
    }
}
