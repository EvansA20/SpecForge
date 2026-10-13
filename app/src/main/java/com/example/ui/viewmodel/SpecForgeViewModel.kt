package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SpecForgeDatabase
import com.example.data.model.DocumentRevision
import com.example.data.model.DocumentSection
import com.example.data.model.Project
import com.example.data.model.Requirement
import com.example.data.model.SrsDocument
import com.example.data.model.TemplateCatalog
import com.example.data.model.TemplatePreset
import com.example.data.model.UseCase
import com.example.data.model.User
import com.example.data.model.ValidationReport
import com.example.data.repository.SpecForgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppScreen {
    DASHBOARD,
    PROJECTS,
    SRS_EDITOR,
    REQUIREMENTS,
    VALIDATION,
    DOCUMENT_PREVIEW,
    TEMPLATES,
    VERSION_HISTORY,
    PROFILE_SETTINGS,
    AUTH
}

class SpecForgeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SpecForgeDatabase.getInstance(application)
    val repository = SpecForgeRepository(database.specForgeDao(), application)

    // Current navigation destination
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Navigation back stack for hardware & custom back handling
    private val backStack = mutableListOf<AppScreen>()

    // Selected Project & Document IDs
    private val _selectedProjectId = MutableStateFlow<String?>(null)
    val selectedProjectId: StateFlow<String?> = _selectedProjectId.asStateFlow()

    private val _selectedDocumentId = MutableStateFlow<String?>(null)
    val selectedDocumentId: StateFlow<String?> = _selectedDocumentId.asStateFlow()

    // User session
    val currentUser: StateFlow<User?> = repository.currentUser
    val isSaving: StateFlow<Boolean> = repository.isSaving
    val lastSaveTime: StateFlow<Long> = repository.lastSaveTime

    // DataStore Offline Caching & Sync States
    val isOnline: StateFlow<Boolean> = repository.isOnline
    val isSimulatedOffline: StateFlow<Boolean> = repository.isSimulatedOffline
    val lastSyncTime: StateFlow<Long> = repository.lastSyncTime
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis())
    val pendingMutationsCount: StateFlow<Int> = repository.pendingMutationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val cachedDocuments: StateFlow<List<SrsDocument>> = repository.cachedDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // All projects
    val allProjects: StateFlow<List<Project>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All documents
    val allDocuments: StateFlow<List<SrsDocument>> = repository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active project and active document derived flows
    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    private val _activeDocument = MutableStateFlow<SrsDocument?>(null)
    val activeDocument: StateFlow<SrsDocument?> = _activeDocument.asStateFlow()

    // Active document's sections, requirements, use cases, revisions
    private val _activeSections = MutableStateFlow<List<DocumentSection>>(emptyList())
    val activeSections: StateFlow<List<DocumentSection>> = _activeSections.asStateFlow()

    private val _activeRequirements = MutableStateFlow<List<Requirement>>(emptyList())
    val activeRequirements: StateFlow<List<Requirement>> = _activeRequirements.asStateFlow()

    private val _activeUseCases = MutableStateFlow<List<UseCase>>(emptyList())
    val activeUseCases: StateFlow<List<UseCase>> = _activeUseCases.asStateFlow()

    private val _activeRevisions = MutableStateFlow<List<DocumentRevision>>(emptyList())
    val activeRevisions: StateFlow<List<DocumentRevision>> = _activeRevisions.asStateFlow()

    // Validation Report
    private val _validationReport = MutableStateFlow<ValidationReport?>(null)
    val validationReport: StateFlow<ValidationReport?> = _validationReport.asStateFlow()

    // UI Feedback & Notifications
    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    // Export progress & result
    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _lastExportedFile = MutableStateFlow<File?>(null)
    val lastExportedFile: StateFlow<File?> = _lastExportedFile.asStateFlow()

    // Search & Filter state
    val projectSearchQuery = MutableStateFlow("")
    val projectStatusFilter = MutableStateFlow("All")

    val requirementSearchQuery = MutableStateFlow("")
    val requirementTypeFilter = MutableStateFlow("All")
    val requirementPriorityFilter = MutableStateFlow("All")
    val requirementStatusFilter = MutableStateFlow("All")

    init {
        // Automatically sync active project and document when project list changes
        viewModelScope.launch {
            allProjects.collect { projects ->
                if (projects.isNotEmpty()) {
                    if (_selectedProjectId.value == null || projects.none { it.id == _selectedProjectId.value }) {
                        selectProject(projects.first().id)
                    } else {
                        _activeProject.value = projects.firstOrNull { it.id == _selectedProjectId.value }
                    }
                } else {
                    _activeProject.value = null
                    _selectedProjectId.value = null
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
            if (screen == AppScreen.VALIDATION || screen == AppScreen.DOCUMENT_PREVIEW) {
                runValidation()
            }
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        if (_currentScreen.value != AppScreen.DASHBOARD) {
            _currentScreen.value = AppScreen.DASHBOARD
            return true
        }
        return false
    }

    fun showMessage(msg: String) {
        _snackMessage.value = msg
    }

    fun clearSnackMessage() {
        _snackMessage.value = null
    }

    fun selectProject(projectId: String) {
        _selectedProjectId.value = projectId
        viewModelScope.launch {
            val project = repository.getProjectById(projectId)
            _activeProject.value = project

            // Fetch documents for this project
            val docs = repository.getDocumentsByProject(projectId).firstOrNull() ?: emptyList()
            if (docs.isNotEmpty()) {
                selectDocument(docs.first().id)
            } else {
                _selectedDocumentId.value = null
                _activeDocument.value = null
                _activeSections.value = emptyList()
                _activeRequirements.value = emptyList()
            }
        }
    }

    fun selectDocument(documentId: String) {
        _selectedDocumentId.value = documentId
        viewModelScope.launch {
            val doc = repository.getDocumentById(documentId)
            _activeDocument.value = doc

            // Observe sections
            launch {
                repository.getSectionsByDocument(documentId).collect {
                    _activeSections.value = it
                }
            }
            // Observe requirements
            launch {
                repository.getRequirementsByDocument(documentId).collect {
                    _activeRequirements.value = it
                }
            }
            // Observe use cases
            launch {
                repository.getUseCasesByDocument(documentId).collect {
                    _activeUseCases.value = it
                }
            }
            // Observe revisions
            launch {
                repository.getRevisionsByDocument(documentId).collect {
                    _activeRevisions.value = it
                }
            }
        }
    }

    // --- Actions: Projects ---
    fun createProject(
        title: String,
        keyPrefix: String,
        description: String,
        category: String,
        targetAudience: String,
        techStack: String
    ) {
        viewModelScope.launch {
            val project = repository.createProject(
                title = title,
                keyPrefix = keyPrefix,
                description = description,
                category = category,
                targetAudience = targetAudience,
                techStack = techStack
            )
            selectProject(project.id)
            showMessage("Project '${project.title}' created successfully!")
            navigateTo(AppScreen.SRS_EDITOR)
        }
    }

    fun createProjectFromTemplate(preset: TemplatePreset) {
        viewModelScope.launch {
            val project = repository.createProjectFromTemplate(preset)
            selectProject(project.id)
            showMessage("Created project from '${preset.name}' template!")
            navigateTo(AppScreen.SRS_EDITOR)
        }
    }

    fun duplicateProject(project: Project) {
        viewModelScope.launch {
            val cloned = repository.duplicateProject(project)
            selectProject(cloned.id)
            showMessage("Duplicated project as '${cloned.title}'")
        }
    }

    fun toggleArchiveProject(project: Project) {
        viewModelScope.launch {
            val newStatus = if (project.status == "Archived") "Active" else "Archived"
            repository.updateProject(project.copy(status = newStatus))
            showMessage("Project status updated to $newStatus")
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
            showMessage("Project '${project.title}' deleted.")
            val remaining = repository.getAllProjects().firstOrNull() ?: emptyList()
            if (remaining.isNotEmpty()) {
                selectProject(remaining.first().id)
            } else {
                _selectedProjectId.value = null
                _activeProject.value = null
            }
        }
    }

    // --- Actions: Sections ---
    fun saveSection(section: DocumentSection) {
        viewModelScope.launch {
            repository.saveSection(section)
        }
    }

    fun addSection(title: String, sectionNumber: String, content: String) {
        val docId = _selectedDocumentId.value ?: return
        viewModelScope.launch {
            val newSection = DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                sectionNumber = sectionNumber,
                title = title,
                content = content,
                orderIndex = (_activeSections.value.maxOfOrNull { it.orderIndex } ?: 0) + 1
            )
            repository.saveSection(newSection)
            showMessage("Section $sectionNumber added.")
        }
    }

    fun deleteSection(section: DocumentSection) {
        viewModelScope.launch {
            repository.deleteSection(section)
            showMessage("Section ${section.sectionNumber} removed.")
        }
    }

    // --- Actions: Requirements ---
    fun saveRequirement(requirement: Requirement) {
        viewModelScope.launch {
            repository.saveRequirement(requirement)
            showMessage("Requirement ${requirement.reqId} saved.")
        }
    }

    fun addRequirement(
        title: String,
        statement: String,
        type: String,
        priority: String,
        acceptanceCriteria: String,
        verificationMethod: String,
        linkedUseCases: String
    ) {
        val docId = _selectedDocumentId.value ?: return
        val projectKey = _activeProject.value?.keyPrefix ?: "REQ"
        val isFunctional = type.equals("Functional", ignoreCase = true)

        viewModelScope.launch {
            val generatedId = repository.generateNextRequirementId(docId, projectKey, isFunctional)
            val newReq = Requirement(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                reqId = generatedId,
                title = title,
                statement = statement,
                type = type,
                priority = priority,
                acceptanceCriteria = acceptanceCriteria,
                verificationMethod = verificationMethod,
                linkedUseCases = linkedUseCases,
                orderIndex = (_activeRequirements.value.maxOfOrNull { it.orderIndex } ?: 0) + 1
            )
            repository.saveRequirement(newReq)
            showMessage("Requirement $generatedId added.")
        }
    }

    fun deleteRequirement(requirement: Requirement) {
        viewModelScope.launch {
            repository.deleteRequirement(requirement)
            showMessage("Requirement ${requirement.reqId} deleted.")
        }
    }

    // --- Actions: Validation ---
    fun runValidation() {
        val docId = _selectedDocumentId.value ?: return
        viewModelScope.launch {
            val report = repository.runDocumentValidation(docId)
            _validationReport.value = report
        }
    }

    // --- Actions: Revision History ---
    fun createRevisionSnapshot(versionTag: String, summary: String) {
        val docId = _selectedDocumentId.value ?: return
        val author = currentUser.value?.fullName ?: "Lead Architect"
        viewModelScope.launch {
            repository.createRevisionSnapshot(docId, versionTag, summary, author)
            showMessage("Revision snapshot $versionTag created!")
        }
    }

    // --- Actions: Export ---
    fun exportToPdf() {
        val docId = _selectedDocumentId.value ?: return
        viewModelScope.launch {
            _isExporting.value = true
            val result = repository.exportPdf(docId)
            _isExporting.value = false
            result.onSuccess { file ->
                _lastExportedFile.value = file
                showMessage("PDF exported: ${file.name}")
                repository.openOrShareFile(file, "application/pdf", isShare = false)
            }.onFailure { err ->
                showMessage("PDF export error: ${err.message}")
            }
        }
    }

    fun exportToDocx() {
        val docId = _selectedDocumentId.value ?: return
        viewModelScope.launch {
            _isExporting.value = true
            val result = repository.exportDocx(docId)
            _isExporting.value = false
            result.onSuccess { file ->
                _lastExportedFile.value = file
                showMessage("DOCX exported: ${file.name}")
                repository.openOrShareFile(file, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", isShare = false)
            }.onFailure { err ->
                showMessage("DOCX export error: ${err.message}")
            }
        }
    }

    fun shareExportedFile(file: File) {
        val mime = if (file.name.endsWith(".pdf")) "application/pdf" else "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        repository.openOrShareFile(file, mime, isShare = true)
    }

    // --- Auth actions ---
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginUser(email, pass)
            result.onSuccess {
                onResult(true, null)
                showMessage("Welcome back, ${it.fullName}!")
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    fun register(
        email: String,
        pass: String,
        name: String,
        org: String,
        role: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(email, pass, name, org, role)
            result.onSuccess {
                onResult(true, null)
                showMessage("Welcome to SpecForge, ${it.fullName}!")
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    fun resetPassword(email: String, newPass: String, answer: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.resetPassword(email, newPass, answer)
            result.onSuccess {
                onResult(true, null)
                showMessage("Password reset successfully. Please sign in.")
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    fun quickDemoLogin() {
        repository.quickDemoLogin()
        showMessage("Signed in with Demonstration Profile.")
        navigateTo(AppScreen.DASHBOARD)
    }

    fun logout() {
        repository.logout()
        showMessage("Logged out.")
        navigateTo(AppScreen.AUTH)
    }

    // --- DataStore Caching & Sync Methods ---
    fun toggleSimulateOffline() {
        val next = !repository.isSimulatedOffline.value
        repository.setSimulateOffline(next)
        if (next) {
            showMessage("Offline Mode: Viewing local DataStore cached SRS documents.")
        } else {
            showMessage("Online Mode Restored: Connecting & synchronizing...")
            syncNow()
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _isSyncing.value = true
            val count = repository.syncPendingChanges()
            _isSyncing.value = false
            if (count > 0) {
                showMessage("Sync Complete: Pushed $count offline changes.")
            } else {
                showMessage("Cache in sync with master database.")
            }
        }
    }

    fun forceRefreshDataStoreCache() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.refreshDataStoreCache()
            _isSyncing.value = false
            showMessage("DataStore SRS cache snapshot refreshed.")
        }
    }
}
