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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Requirement
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
import com.example.ui.viewmodel.SpecForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequirementsScreen(
    viewModel: SpecForgeViewModel,
    modifier: Modifier = Modifier
) {
    val activeProject by viewModel.activeProject.collectAsState()
    val activeDocument by viewModel.activeDocument.collectAsState()
    val requirements by viewModel.activeRequirements.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("All") }
    var selectedPriorityFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingRequirement by remember { mutableStateOf<Requirement?>(null) }
    var reqToDelete by remember { mutableStateOf<Requirement?>(null) }

    val typeFilters = listOf("All", "Functional", "Non-Functional", "Security", "Performance", "Interface")
    val priorityFilters = listOf("All", "Must Have", "Should Have", "Could Have")

    val filteredRequirements = requirements.filter { req ->
        val matchesSearch = req.reqId.contains(searchQuery, ignoreCase = true) ||
                req.title.contains(searchQuery, ignoreCase = true) ||
                req.statement.contains(searchQuery, ignoreCase = true) ||
                req.acceptanceCriteria.contains(searchQuery, ignoreCase = true)
        val matchesType = if (selectedTypeFilter == "All") true else req.type.equals(selectedTypeFilter, ignoreCase = true)
        val matchesPriority = if (selectedPriorityFilter == "All") true else req.priority.equals(selectedPriorityFilter, ignoreCase = true)
        matchesSearch && matchesType && matchesPriority
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
                    text = "Requirements Manager",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${filteredRequirements.size} requirements in ${activeProject?.title ?: "Project"}",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_requirement_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Requirement", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by ID (e.g. REQ-F-001) or title...", color = SlateMuted, fontSize = 13.sp) },
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
                .testTag("requirements_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(typeFilters) { type ->
                val selected = selectedTypeFilter == type
                FilterChip(
                    selected = selected,
                    onClick = { selectedTypeFilter = type },
                    label = { Text(type, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
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

        // Requirements List
        if (filteredRequirements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No requirements match your criteria", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Click 'Add Requirement' to draft a new formal IEEE specification.", color = SlateLight, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("Add Requirement")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRequirements) { req ->
                    RequirementItemCard(
                        requirement = req,
                        onEdit = { editingRequirement = req },
                        onDelete = { reqToDelete = req }
                    )
                }
            }
        }
    }

    // Add Requirement Dialog
    if (showAddDialog) {
        RequirementFormDialog(
            initialRequirement = null,
            onDismiss = { showAddDialog = false },
            onSave = { title, stmt, type, priority, ac, vm, uc ->
                viewModel.addRequirement(title, stmt, type, priority, ac, vm, uc)
                showAddDialog = false
            }
        )
    }

    // Edit Requirement Dialog
    if (editingRequirement != null) {
        RequirementFormDialog(
            initialRequirement = editingRequirement,
            onDismiss = { editingRequirement = null },
            onSave = { title, stmt, type, priority, ac, vm, uc ->
                viewModel.saveRequirement(
                    editingRequirement!!.copy(
                        title = title,
                        statement = stmt,
                        type = type,
                        priority = priority,
                        acceptanceCriteria = ac,
                        verificationMethod = vm,
                        linkedUseCases = uc
                    )
                )
                editingRequirement = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (reqToDelete != null) {
        AlertDialog(
            onDismissRequest = { reqToDelete = null },
            containerColor = NavyDark,
            title = { Text("Delete Requirement?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete requirement '${reqToDelete?.reqId}: ${reqToDelete?.title}'?",
                    color = SlateLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        reqToDelete?.let { viewModel.deleteRequirement(it) }
                        reqToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRose)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { reqToDelete = null }) {
                    Text("Cancel", color = SlateLight)
                }
            }
        )
    }
}

@Composable
fun RequirementItemCard(
    requirement: Requirement,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Req ID, Priority Chip, Type Chip, Actions
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
                            text = requirement.reqId,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricBlueLight,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = when (requirement.priority) {
                            "Must Have" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                            "Should Have" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                            else -> SlateMuted.copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = requirement.priority,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (requirement.priority) {
                                "Must Have" -> Color(0xFFEF4444)
                                "Should Have" -> Color(0xFFF59E0B)
                                else -> SlateLight
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        color = NavyCard,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = requirement.type,
                            fontSize = 10.sp,
                            color = SlateLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SlateLight, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRose, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = requirement.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Statement
            Text(
                text = requirement.statement,
                fontSize = 13.sp,
                color = SlateLight,
                lineHeight = 18.sp
            )

            // Acceptance criteria
            if (requirement.acceptanceCriteria.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Acceptance Criteria:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlueLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = requirement.acceptanceCriteria,
                            fontSize = 11.sp,
                            color = SlateLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer info: Verification & Linked Use Cases
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Verification: ${requirement.verificationMethod}",
                    fontSize = 10.sp,
                    color = SlateMuted
                )

                if (requirement.linkedUseCases.isNotBlank()) {
                    Text(
                        text = "Trace: ${requirement.linkedUseCases}",
                        fontSize = 10.sp,
                        color = AccentCyan
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequirementFormDialog(
    initialRequirement: Requirement?,
    onDismiss: () -> Unit,
    onSave: (title: String, statement: String, type: String, priority: String, ac: String, vm: String, uc: String) -> Unit
) {
    var title by remember { mutableStateOf(initialRequirement?.title ?: "") }
    var statement by remember {
        mutableStateOf(
            initialRequirement?.statement ?: "The system shall [specific behavior] under [defined condition]."
        )
    }
    var type by remember { mutableStateOf(initialRequirement?.type ?: "Functional") }
    var priority by remember { mutableStateOf(initialRequirement?.priority ?: "Must Have") }
    var acceptanceCriteria by remember { mutableStateOf(initialRequirement?.acceptanceCriteria ?: "") }
    var verificationMethod by remember { mutableStateOf(initialRequirement?.verificationMethod ?: "Test") }
    var linkedUseCases by remember { mutableStateOf(initialRequirement?.linkedUseCases ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val types = listOf("Functional", "Non-Functional", "Security", "Performance", "Interface", "Data")
    val priorities = listOf("Must Have", "Should Have", "Could Have", "Won't Have")
    val verifications = listOf("Test", "Demonstration", "Inspection", "Analysis")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavyDark,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                if (initialRequirement == null) "New Specification Requirement" else "Edit Requirement ${initialRequirement.reqId}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Requirement Title *", color = SlateLight) },
                        placeholder = { Text("e.g. End-to-End Cart Checkout", color = SlateMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("req_form_title_input")
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Type Selection
                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Type", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Priority Selection
                        OutlinedTextField(
                            value = priority,
                            onValueChange = { priority = it },
                            label = { Text("Priority", color = SlateLight) },
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
                }

                // IEEE Statement Helper
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Specification Statement *", color = SlateLight, fontSize = 12.sp)
                            TextButton(
                                onClick = {
                                    statement = "The system shall [specific behavior] under [defined condition]."
                                },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Insert Template", color = ElectricBlueLight, fontSize = 10.sp)
                            }
                        }

                        OutlinedTextField(
                            value = statement,
                            onValueChange = { statement = it },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("req_form_stmt_input")
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = acceptanceCriteria,
                        onValueChange = { acceptanceCriteria = it },
                        label = { Text("Acceptance Criteria (Given-When-Then)", color = SlateLight) },
                        placeholder = { Text("Given authenticated user, when checkout clicked, then order commits...", color = SlateMuted) },
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("req_form_ac_input")
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = verificationMethod,
                            onValueChange = { verificationMethod = it },
                            label = { Text("Verification Method", color = SlateLight) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = linkedUseCases,
                            onValueChange = { linkedUseCases = it },
                            label = { Text("Linked Use Cases", color = SlateLight) },
                            placeholder = { Text("e.g. UC-001", color = SlateMuted) },
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
                    if (title.isBlank() || statement.isBlank()) {
                        errorMessage = "Title and specification statement are required."
                    } else {
                        onSave(title.trim(), statement.trim(), type.trim(), priority.trim(), acceptanceCriteria.trim(), verificationMethod.trim(), linkedUseCases.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier.testTag("req_form_save_btn")
            ) {
                Text("Save Requirement", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateLight)
            }
        }
    )
}
