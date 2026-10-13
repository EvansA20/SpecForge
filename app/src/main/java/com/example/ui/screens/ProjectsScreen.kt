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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    val allProjects by viewModel.allProjects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    val statusTabs = listOf("All", "Active", "Planning", "In Review", "Archived")

    val filteredProjects = allProjects.filter { p ->
        val matchesSearch = p.title.contains(searchQuery, ignoreCase = true) ||
                p.keyPrefix.contains(searchQuery, ignoreCase = true) ||
                p.category.contains(searchQuery, ignoreCase = true) ||
                p.description.contains(searchQuery, ignoreCase = true)
        val matchesStatus = if (selectedStatusFilter == "All") true else p.status.equals(selectedStatusFilter, ignoreCase = true)
        matchesSearch && matchesStatus
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
                    text = "Software Projects",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${filteredProjects.size} of ${allProjects.size} projects",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("projects_create_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by title, prefix, tech stack...", color = SlateMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SlateLight) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
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
                .testTag("projects_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            statusTabs.forEach { tab ->
                val selected = selectedStatusFilter == tab
                FilterChip(
                    selected = selected,
                    onClick = { selectedStatusFilter = tab },
                    label = { Text(tab, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
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

        // Project List
        if (filteredProjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No projects match your filter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Try clearing your search query or create a new project.", color = SlateLight, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("Create Project")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProjects) { project ->
                    ProjectDetailCard(
                        project = project,
                        isSelected = project.id == activeProject?.id,
                        onSelect = {
                            viewModel.selectProject(project.id)
                            viewModel.navigateTo(AppScreen.SRS_EDITOR)
                        },
                        onDuplicate = { viewModel.duplicateProject(project) },
                        onToggleArchive = { viewModel.toggleArchiveProject(project) },
                        onDelete = { projectToDelete = project }
                    )
                }
            }
        }
    }

    // Create Project Dialog
    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, prefix, desc, cat, audience, stack ->
                viewModel.createProject(title, prefix, desc, cat, audience, stack)
                showCreateDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            containerColor = NavyDark,
            title = { Text("Delete Project?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to permanently delete '${projectToDelete?.title}' and all its associated SRS documents and requirements? This action cannot be undone.",
                    color = SlateLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { viewModel.deleteProject(it) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRose)
                ) {
                    Text("Delete Permanently", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Cancel", color = SlateLight)
                }
            }
        )
    }
}

@Composable
fun ProjectDetailCard(
    project: Project,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDuplicate: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) NavyCard else NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricBlue else SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ElectricBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = project.keyPrefix,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlueLight,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = project.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = SlateLight)
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(NavyDark)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open & Edit SRS", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = ElectricBlueLight) },
                            onClick = {
                                menuExpanded = false
                                onSelect()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate Project", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White) },
                            onClick = {
                                menuExpanded = false
                                onDuplicate()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (project.status == "Archived") "Unarchive Project" else "Archive Project",
                                    color = Color.White
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    if (project.status == "Archived") Icons.Default.Unarchive else Icons.Default.Archive,
                                    contentDescription = null,
                                    tint = SlateLight
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onToggleArchive()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Project", color = AccentRose) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = AccentRose) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.description,
                color = SlateLight,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tech Stack & Metadata row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Stack: ${project.techStack}",
                    color = SlateMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = when (project.status) {
                        "Active" -> Color(0xFF10B981).copy(alpha = 0.15f)
                        "Archived" -> SlateMuted.copy(alpha = 0.2f)
                        else -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = project.status,
                        fontSize = 10.sp,
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

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Audience: ${project.targetAudience}",
                    color = SlateLight,
                    fontSize = 10.sp
                )
                Text(
                    text = "Updated: ${dateFormat.format(Date(project.updatedAt))}",
                    color = SlateMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, prefix: String, desc: String, category: String, audience: String, techStack: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var prefix by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Web SaaS") }
    var audience by remember { mutableStateOf("Enterprise Stakeholders & Developers") }
    var techStack by remember { mutableStateOf("Kotlin, Next.js, PostgreSQL, REST APIs") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Web SaaS", "Mobile App", "Academic Capstone", "Microservices API", "Enterprise ERP", "Commercial Retail")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavyDark,
        shape = RoundedCornerShape(16.dp),
        title = { Text("Create Software Project", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (prefix.isBlank() && it.isNotBlank()) {
                                prefix = it.split(" ").take(2).mapNotNull { word -> word.firstOrNull()?.uppercase() }.joinToString("").take(4)
                            }
                        },
                        label = { Text("Project Title *", color = SlateLight) },
                        placeholder = { Text("e.g. Apex Health EHR System", color = SlateMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_project_title_input")
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = prefix,
                            onValueChange = { if (it.length <= 5) prefix = it.uppercase() },
                            label = { Text("Key Prefix *", color = SlateLight) },
                            placeholder = { Text("e.g. APX", color = SlateMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("create_project_prefix_input")
                        )

                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Scope & Purpose Description *", color = SlateLight) },
                        placeholder = { Text("Describe the system purpose, goals, and problem solved...", color = SlateMuted) },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_project_desc_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = audience,
                        onValueChange = { audience = it },
                        label = { Text("Target Audience & User Classes", color = SlateLight) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = techStack,
                        onValueChange = { techStack = it },
                        label = { Text("Tech Stack & Architecture", color = SlateLight) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage != null) {
                    item {
                        Text(errorMessage!!, color = AccentRose, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || prefix.isBlank() || description.isBlank()) {
                        errorMessage = "Please fill in title, key prefix, and description."
                    } else {
                        onCreate(title.trim(), prefix.trim(), description.trim(), category.trim(), audience.trim(), techStack.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier.testTag("create_project_confirm_btn")
            ) {
                Text("Create Project", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateLight)
            }
        }
    )
}
