package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavyMedium
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpecForgeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: SpecForgeViewModel,
    onOpenCreateProjectDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allProjects by viewModel.allProjects.collectAsState()
    val allDocuments by viewModel.allDocuments.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val activeDocument by viewModel.activeDocument.collectAsState()
    val activeRequirements by viewModel.activeRequirements.collectAsState()
    val validationReport by viewModel.validationReport.collectAsState()

    LaunchedEffect(activeDocument?.id) {
        viewModel.runValidation()
    }

    val functionalCount = activeRequirements.count { it.type.equals("Functional", ignoreCase = true) }
    val nonFunctionalCount = activeRequirements.size - functionalCount

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero SaaS Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NavySurface, NavyCard.copy(alpha = 0.8f), ElectricBlue.copy(alpha = 0.15f))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SRS Command Center",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "IEEE 830 & ISO 29148 Software Specifications Engine",
                                    fontSize = 12.sp,
                                    color = ElectricBlueLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Surface(
                                color = ElectricBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "ISO COMPLIANT",
                                    color = ElectricBlueLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Draft testable requirements, validate completeness in real-time, link use cases to traceability matrices, and compile official PDF/DOCX deliverables.",
                            fontSize = 13.sp,
                            color = SlateLight,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Founder & WebForge Team Badge
                        Surface(
                            color = NavyDark,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(AppScreen.PROFILE_SETTINGS) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AccentEmerald)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Built by Evans Ayanlude Abayomi • Founder & CEO, WebForge Team",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Cyber Security Science (LAUTECH) • Build • Secure • Innovate",
                                        fontSize = 10.sp,
                                        color = ElectricBlueLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onOpenCreateProjectDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("dashboard_new_project_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Project", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(AppScreen.TEMPLATES) },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlueLight),
                                modifier = Modifier.testTag("dashboard_browse_templates_btn")
                            ) {
                                Icon(Icons.Default.Layers, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Use Template", color = ElectricBlueLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Metrics Grid (4 Stat Cards)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        title = "Total Projects",
                        value = "${allProjects.size}",
                        subtitle = "${allProjects.count { it.status == "Active" }} Active",
                        icon = Icons.Default.Folder,
                        accentColor = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "SRS Documents",
                        value = "${allDocuments.size}",
                        subtitle = "Version ${activeDocument?.version ?: "1.0.0"}",
                        icon = Icons.Default.Description,
                        accentColor = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        title = "Requirements",
                        value = "${activeRequirements.size}",
                        subtitle = "$functionalCount Func / $nonFunctionalCount NFR",
                        icon = Icons.Default.FactCheck,
                        accentColor = AccentEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Quality Health",
                        value = "${validationReport?.score ?: 95}%",
                        subtitle = "Grade: ${validationReport?.grade ?: "A+"}",
                        icon = Icons.Default.Verified,
                        accentColor = if ((validationReport?.errorCount ?: 0) > 0) AccentRose else AccentEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Active Project Focus Card
        item {
            if (activeProject != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElectricBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeProject!!.keyPrefix.take(3),
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlueLight,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = activeProject!!.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${activeProject!!.category} • Status: ${activeProject!!.status}",
                                        fontSize = 11.sp,
                                        color = SlateLight
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.SRS_EDITOR) },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit SRS", color = ElectricBlueLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = activeProject!!.description,
                            fontSize = 12.sp,
                            color = SlateLight,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        // Progress bar towards completeness
                        val score = validationReport?.score ?: 90
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Specification Completeness",
                                fontSize = 11.sp,
                                color = SlateLight
                            )
                            Text(
                                text = "$score%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlueLight
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { score / 100f },
                            color = ElectricBlue,
                            trackColor = SlateBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(AppScreen.REQUIREMENTS) },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Requirements (${activeRequirements.size})", fontSize = 11.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(AppScreen.VALIDATION) },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Validate Document", fontSize = 11.sp, color = Color.White)
                            }

                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.DOCUMENT_PREVIEW) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Export", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Quick Navigation Section
        item {
            Text(
                text = "Projects Directory",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Project Items List
        items(allProjects) { project ->
            ProjectRowCard(
                project = project,
                isSelected = project.id == activeProject?.id,
                onSelect = { viewModel.selectProject(project.id) },
                onEdit = {
                    viewModel.selectProject(project.id)
                    viewModel.navigateTo(AppScreen.SRS_EDITOR)
                }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateLight
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = SlateMuted
            )
        }
    }
}

@Composable
fun ProjectRowCard(
    project: Project,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NavyCard else NavySurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) ElectricBlue else SlateBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ElectricBlue else NavyMedium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = project.keyPrefix,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = when (project.status) {
                            "Active" -> Color(0xFF10B981).copy(alpha = 0.2f)
                            "Archived" -> SlateMuted.copy(alpha = 0.2f)
                            else -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = project.status,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (project.status) {
                                "Active" -> Color(0xFF10B981)
                                "Archived" -> SlateLight
                                else -> Color(0xFFF59E0B)
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = project.category,
                    fontSize = 11.sp,
                    color = SlateLight
                )
            }

            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(containerColor = NavyMedium),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Open", fontSize = 11.sp, color = ElectricBlueLight)
            }
        }
    }
}
