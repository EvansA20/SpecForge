package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ValidationIssue
import com.example.data.model.ValidationSeverity
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpecForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidationScreen(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    val report by viewModel.validationReport.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val activeDocument by viewModel.activeDocument.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(activeDocument?.id) {
        viewModel.runValidation()
    }

    val issues = report?.issues ?: emptyList()
    val filteredIssues = issues.filter { issue ->
        when (selectedFilter) {
            "Errors" -> issue.severity == ValidationSeverity.ERROR
            "Warnings" -> issue.severity == ValidationSeverity.WARNING
            "Notices" -> issue.severity == ValidationSeverity.INFO
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "SRS Validation Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Specification quality audit for ${activeProject?.title ?: "Document"}",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }

            Button(
                onClick = { viewModel.runValidation() },
                colors = ButtonDefaults.buttonColors(containerColor = NavySurface),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.testTag("validation_recheck_btn")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Re-Audit", color = ElectricBlueLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Health Score Summary Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(18.dp)
            ) {
                // Score Donut Box
                val score = report?.score ?: 95
                val grade = report?.grade ?: "A+"
                val gradeColor = when {
                    score >= 90 -> AccentEmerald
                    score >= 75 -> AccentCyan
                    score >= 60 -> Color(0xFFF59E0B)
                    else -> AccentRose
                }

                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(gradeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = gradeColor
                        )
                        Text(
                            text = grade,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = gradeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if ((report?.errorCount ?: 0) == 0) "Audit Passed: Ready for Review" else "Action Required",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scanned against IEEE 830-1998 standards, ambiguity heuristics, and acceptance testability rules.",
                        fontSize = 11.sp,
                        color = SlateLight,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "${report?.errorCount ?: 0} Errors",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentRose
                        )
                        Text(
                            text = "${report?.warningCount ?: 0} Warnings",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B)
                        )
                        Text(
                            text = "${report?.infoCount ?: 0} Notices",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("All", "Errors", "Warnings", "Notices").forEach { tab ->
                val selected = selectedFilter == tab
                FilterChip(
                    selected = selected,
                    onClick = { selectedFilter = tab },
                    label = { Text(tab, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = Color.White,
                        containerColor = NavySurface,
                        labelColor = SlateLight
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = if (selected) ElectricBlue else SlateBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Issues List
        if (filteredIssues.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AccentEmerald,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No issues detected in this category!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Your specification conforms to formal engineering completeness rules.", color = SlateLight, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredIssues) { issue ->
                    ValidationIssueCard(
                        issue = issue,
                        onFixInEditor = { viewModel.navigateTo(AppScreen.SRS_EDITOR) },
                        onFixInRequirements = { viewModel.navigateTo(AppScreen.REQUIREMENTS) }
                    )
                }
            }
        }
    }
}

@Composable
fun ValidationIssueCard(
    issue: ValidationIssue,
    onFixInEditor: () -> Unit,
    onFixInRequirements: () -> Unit
) {
    val (severityColor, severityLabel, severityIcon) = when (issue.severity) {
        ValidationSeverity.ERROR -> Triple(AccentRose, "ERROR", Icons.Default.Error)
        ValidationSeverity.WARNING -> Triple(Color(0xFFF59E0B), "WARNING", Icons.Default.Warning)
        ValidationSeverity.INFO -> Triple(AccentCyan, "NOTICE", Icons.Default.Info)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, severityColor.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Severity & Category Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = severityColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(severityIcon, contentDescription = null, tint = severityColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = severityLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = severityColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = issue.category,
                        fontSize = 11.sp,
                        color = SlateMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (issue.requirementId != null) {
                    Surface(
                        color = ElectricBlue.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = issue.requirementId,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlueLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (issue.sectionNumber != null) {
                    Text(
                        text = "Sec ${issue.sectionNumber}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Issue Title
            Text(
                text = issue.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = issue.description,
                fontSize = 12.sp,
                color = SlateLight,
                lineHeight = 16.sp
            )

            // Recommendation / Suggestion Box
            if (issue.suggestion.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Rule, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Standard Remediation:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlueLight
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = issue.suggestion,
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Direct Fix CTA
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (issue.requirementId != null) {
                    OutlinedButton(
                        onClick = onFixInRequirements,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlueLight)
                    ) {
                        Text("Fix in Requirements", fontSize = 11.sp, color = ElectricBlueLight)
                    }
                } else {
                    OutlinedButton(
                        onClick = onFixInEditor,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlueLight)
                    ) {
                        Text("Fix in Section Editor", fontSize = 11.sp, color = ElectricBlueLight)
                    }
                }
            }
        }
    }
}
