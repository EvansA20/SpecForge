package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
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
import com.example.ui.viewmodel.SpecForgeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DocumentPreviewScreen(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    val activeProject by viewModel.activeProject.collectAsState()
    val activeDocument by viewModel.activeDocument.collectAsState()
    val sections by viewModel.activeSections.collectAsState()
    val requirements by viewModel.activeRequirements.collectAsState()
    val useCases by viewModel.activeUseCases.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val lastExportedFile by viewModel.lastExportedFile.collectAsState()

    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
    val pubDate = dateFormat.format(Date(activeDocument?.lastModified ?: System.currentTimeMillis()))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(16.dp)
    ) {
        // Export Action Bar
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(14.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Document Export Center",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Generate real, formatted PDF and DOCX files for stakeholders",
                        fontSize = 11.sp,
                        color = SlateLight
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.exportToPdf() },
                        enabled = !isExporting,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_pdf_button")
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(14.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.exportToDocx() },
                        enabled = !isExporting,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_docx_button")
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(14.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DOCX", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Export Success Notification & Share Box
        if (lastExportedFile != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = AccentEmerald.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Exported: ${lastExportedFile?.name}",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = { lastExportedFile?.let { viewModel.shareExportedFile(it) } },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Printable Document Simulation Container
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Document Cover Page View
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Top Accent Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(ElectricBlue)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "SOFTWARE REQUIREMENTS SPECIFICATION",
                            color = ElectricBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "IEEE Std 830-1998 / ISO 29148 Compliant Specification",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = activeProject?.title ?: "Software System",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0B132B)
                        )

                        Text(
                            text = "${activeDocument?.title ?: "SRS"} — Version ${activeDocument?.version ?: "1.0.0"}",
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Metadata Grid
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                MetaRow(label = "Project Key", value = activeProject?.keyPrefix ?: "SFG")
                                MetaRow(label = "Category", value = activeProject?.category ?: "SaaS")
                                MetaRow(label = "Author / Architect", value = activeDocument?.author ?: "Lead Architect")
                                MetaRow(label = "Organization", value = activeDocument?.organization ?: "Engineering Dept")
                                MetaRow(label = "Publication Date", value = pubDate)
                                MetaRow(label = "Classification", value = activeDocument?.confidentiality ?: "Confidential")
                                MetaRow(label = "Target Audience", value = activeProject?.targetAudience ?: "Engineers")
                            }
                        }
                    }
                }
            }

            // Table of Contents Preview
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Table of Contents",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B132B)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        sections.filter { it.isEnabled }.forEach { sec ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${sec.sectionNumber}  ${sec.title}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Section",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "4.0  System Requirements Specification Matrix",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E293B)
                            )
                            Text(text = "Table", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }

                        if (useCases.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "5.0  Requirements Traceability & Use Cases",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B)
                                )
                                Text(text = "Table", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            }

            // Document Sections Body Preview
            items(sections.size) { idx ->
                val sec = sections[idx]
                if (sec.isEnabled) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "${sec.sectionNumber} ${sec.title}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0066FF)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = sec.content,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B),
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            // Requirements Table Section Preview
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "4.0 System Requirements Specification Matrix",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0066FF)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        requirements.forEach { req ->
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = req.reqId,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0066FF),
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "[${req.type}] [${req.priority}]",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = req.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = req.statement,
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155),
                                        lineHeight = 16.sp
                                    )
                                    if (req.acceptanceCriteria.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Acceptance: ${req.acceptanceCriteria}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B),
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Traceability Matrix Preview
            if (useCases.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "5.0 Traceability Matrix",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0066FF)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            useCases.forEach { uc ->
                                Surface(
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "${uc.useCaseId}: ${uc.title}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "Actor: ${uc.primaryActor}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                        Text(
                                            text = "Traces: ${uc.linkedRequirementIds}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF0066FF)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetaRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
        Text(text = value, fontSize = 11.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Medium)
    }
}
