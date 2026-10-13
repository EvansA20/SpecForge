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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentSection
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

@Composable
fun SrsEditorScreen(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    val activeProject by viewModel.activeProject.collectAsState()
    val activeDocument by viewModel.activeDocument.collectAsState()
    val sections by viewModel.activeSections.collectAsState()

    var selectedSectionId by remember { mutableStateOf<String?>(null) }
    var currentTitle by remember { mutableStateOf("") }
    var currentContent by remember { mutableStateOf("") }
    var currentSectionNumber by remember { mutableStateOf("") }
    var showAddSectionDialog by remember { mutableStateOf(false) }

    // When sections list updates or changes selection, sync the local editor state
    val selectedSection = sections.firstOrNull { it.id == selectedSectionId } ?: sections.firstOrNull()

    LaunchedEffect(selectedSection?.id) {
        if (selectedSection != null) {
            selectedSectionId = selectedSection.id
            currentTitle = selectedSection.title
            currentContent = selectedSection.content
            currentSectionNumber = selectedSection.sectionNumber
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Guided SRS Editor",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${activeProject?.title ?: "Project"} • Document v${activeDocument?.version ?: "1.0.0"}",
                    fontSize = 12.sp,
                    color = ElectricBlueLight
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.VALIDATION) },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Validate", fontSize = 11.sp, color = Color.White)
                }

                Button(
                    onClick = { viewModel.navigateTo(AppScreen.DOCUMENT_PREVIEW) },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("editor_preview_button")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Preview & Export", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Section Tabs
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(sections) { section ->
                    val isSelected = section.id == selectedSection?.id
                    Surface(
                        color = if (isSelected) ElectricBlue else NavySurface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricBlueLight else SlateBorder),
                        modifier = Modifier
                            .clickable {
                                // Save existing changes before switching
                                if (selectedSection != null && (currentTitle != selectedSection.title || currentContent != selectedSection.content)) {
                                    viewModel.saveSection(selectedSection.copy(title = currentTitle, content = currentContent))
                                }
                                selectedSectionId = section.id
                            }
                            .testTag("section_tab_${section.sectionNumber.replace(".", "_")}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = section.sectionNumber,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else ElectricBlueLight,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = section.title,
                                color = if (isSelected) Color.White else SlateLight,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { showAddSectionDialog = true },
                modifier = Modifier
                    .size(32.dp)
                    .background(NavySurface, RoundedCornerShape(6.dp))
                    .testTag("add_section_tab_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Section", tint = ElectricBlueLight, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Editor Canvas
        if (selectedSection != null) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section Title & Action controls
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = currentSectionNumber,
                                        onValueChange = { currentSectionNumber = it },
                                        label = { Text("Sec #", fontSize = 10.sp) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = ElectricBlue,
                                            unfocusedBorderColor = SlateBorder
                                        ),
                                        modifier = Modifier.width(80.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    OutlinedTextField(
                                        value = currentTitle,
                                        onValueChange = { currentTitle = it },
                                        label = { Text("Section Title", fontSize = 10.sp) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = ElectricBlue,
                                            unfocusedBorderColor = SlateBorder
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            viewModel.saveSection(
                                                selectedSection.copy(
                                                    sectionNumber = currentSectionNumber,
                                                    title = currentTitle,
                                                    content = currentContent
                                                )
                                            )
                                            viewModel.showMessage("Section saved.")
                                        },
                                        modifier = Modifier.testTag("save_section_button")
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = "Save Section", tint = AccentEmerald)
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteSection(selectedSection) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Section", tint = AccentRose)
                                    }
                                }
                            }

                            // Guidance Prompt Tip
                            if (selectedSection.guidancePrompt.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = NavyCard,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = selectedSection.guidancePrompt,
                                            fontSize = 11.sp,
                                            color = SlateLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Helper Snippet Chips
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = NavySurface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                            modifier = Modifier
                                .clickable {
                                    val templateSentence = "\nThe system shall [specific behavior] under [defined condition]."
                                    currentContent += templateSentence
                                    viewModel.saveSection(selectedSection.copy(content = currentContent))
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ IEEE Shall Statement", fontSize = 10.sp, color = ElectricBlueLight, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Surface(
                            color = NavySurface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                            modifier = Modifier
                                .clickable {
                                    val templateSubSection = "\n\n1. Purpose:\n2. Operational Constraints:\n3. Preconditions:\n"
                                    currentContent += templateSubSection
                                    viewModel.saveSection(selectedSection.copy(content = currentContent))
                                }
                        ) {
                            Text("+ Outline Template", fontSize = 10.sp, color = SlateLight, modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        val wordCount = currentContent.split("\\s+".toRegex()).count { it.isNotBlank() }
                        Text(
                            text = "$wordCount words • ${currentContent.length} chars",
                            fontSize = 10.sp,
                            color = SlateMuted
                        )
                    }
                }

                // Body Content TextField
                item {
                    OutlinedTextField(
                        value = currentContent,
                        onValueChange = {
                            currentContent = it
                            // Autosave on change
                            viewModel.saveSection(selectedSection.copy(content = it, title = currentTitle, sectionNumber = currentSectionNumber))
                        },
                        placeholder = { Text("Write technical specifications, system architecture, or operational workflows...", color = SlateMuted) },
                        minLines = 14,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NavySurface,
                            unfocusedContainerColor = NavySurface,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("section_content_editor")
                    )
                }

                // Requirements quick reference
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Column {
                                Text("Requirement Matrix Linkage", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Manage individual functional & non-functional requirements", color = SlateLight, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.REQUIREMENTS) },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Requirements Manager", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No sections found. Click + to add an SRS section.", color = SlateLight)
            }
        }
    }

    // Add Section Dialog
    if (showAddSectionDialog) {
        var newSecNum by remember { mutableStateOf("${sections.size + 1}.0") }
        var newTitle by remember { mutableStateOf("") }
        var newContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSectionDialog = false },
            containerColor = NavyDark,
            title = { Text("Add New SRS Section", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newSecNum,
                        onValueChange = { newSecNum = it },
                        label = { Text("Section Number", color = SlateLight) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Section Title", color = SlateLight) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Initial Content", color = SlateLight) },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addSection(newTitle.trim(), newSecNum.trim(), newContent.trim())
                            showAddSectionDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Add Section")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSectionDialog = false }) {
                    Text("Cancel", color = SlateLight)
                }
            }
        )
    }
}
