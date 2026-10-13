package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.export.DocxExporter
import com.example.data.export.PdfExporter
import com.example.data.local.SpecForgeDao
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
import com.example.data.sync.NetworkMonitor
import com.example.data.sync.SrsDataStoreCache
import com.example.data.validator.SrsValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class SpecForgeRepository(
    private val dao: SpecForgeDao,
    private val context: Context
) {
    val dataStoreCache = SrsDataStoreCache(context)
    val networkMonitor = NetworkMonitor(context)

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
    val isSimulatedOffline: StateFlow<Boolean> = networkMonitor.isSimulatedOffline
    val lastSyncTime: Flow<Long> = dataStoreCache.lastSyncTimeFlow
    val pendingMutationsCount: Flow<Int> = dataStoreCache.pendingMutationsCountFlow
    val cachedDocuments: Flow<List<SrsDocument>> = dataStoreCache.cachedDocumentsFlow

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _lastSaveTime = MutableStateFlow<Long>(System.currentTimeMillis())
    val lastSaveTime: StateFlow<Long> = _lastSaveTime.asStateFlow()

    private val _isSaving = MutableStateFlow<Boolean>(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        // Initialize with default demo session and initial seeded templates
        CoroutineScope(Dispatchers.IO).launch {
            initDefaultDemoUserAndData()
            refreshDataStoreCache()

            // Observe connectivity to auto-sync when online
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    syncPendingChanges()
                }
            }
        }
    }

    private suspend fun initDefaultDemoUserAndData() {
        val evansUser = User(
            id = "user_evans_01",
            email = "eaayanlude@gmail.com",
            passwordHash = "demo123", // For instant showcase sign-in
            fullName = "Evans Ayanlude Abayomi",
            organization = "WebForge Team",
            role = "Founder & CEO | Cyber Security Science (LAUTECH)",
            securityAnswer = "webforge"
        )
        dao.insertUser(evansUser)
        _currentUser.value = evansUser

        // Check if projects exist, if not seed initial showcase project from SaaS template
        val existingProjects = dao.getAllProjectsFlow().first()
        if (existingProjects.isEmpty()) {
            val saasTemplate = TemplateCatalog.presets[1] // SaaS Web App template
            createProjectFromTemplate(saasTemplate, "SpecForge Cloud Orchestrator", "SFO")
        }
    }

    fun setSimulateOffline(simulate: Boolean) {
        networkMonitor.setSimulateOffline(simulate)
    }

    suspend fun refreshDataStoreCache() = withContext(Dispatchers.IO) {
        val documents = dao.getAllDocumentsFlow().firstOrNull() ?: emptyList()
        val sectionsMap = mutableMapOf<String, List<DocumentSection>>()
        val requirementsMap = mutableMapOf<String, List<Requirement>>()

        for (doc in documents) {
            sectionsMap[doc.id] = dao.getSectionsByDocument(doc.id)
            requirementsMap[doc.id] = dao.getRequirementsByDocument(doc.id)
        }

        dataStoreCache.cacheSnapshot(documents, sectionsMap, requirementsMap)
    }

    suspend fun syncPendingChanges(): Int = withContext(Dispatchers.IO) {
        val mutations = dataStoreCache.flushPendingMutations()
        // Refresh local cache snapshot
        refreshDataStoreCache()
        mutations.size
    }

    suspend fun getCachedSections(documentId: String): List<DocumentSection> {
        return if (!networkMonitor.isOnline.value) {
            val cached = dataStoreCache.getCachedSections(documentId)
            if (cached.isNotEmpty()) cached else dao.getSectionsByDocument(documentId)
        } else {
            dao.getSectionsByDocument(documentId)
        }
    }

    suspend fun getCachedRequirements(documentId: String): List<Requirement> {
        return if (!networkMonitor.isOnline.value) {
            val cached = dataStoreCache.getCachedRequirements(documentId)
            if (cached.isNotEmpty()) cached else dao.getRequirementsByDocument(documentId)
        } else {
            dao.getRequirementsByDocument(documentId)
        }
    }

    // --- Authentication ---
    suspend fun registerUser(
        email: String,
        password: String,
        fullName: String,
        organization: String,
        role: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val existing = dao.getUserByEmail(email)
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists."))
        }
        val newUser = User(
            id = UUID.randomUUID().toString(),
            email = email,
            passwordHash = password,
            fullName = fullName,
            organization = organization,
            role = role
        )
        dao.insertUser(newUser)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    suspend fun loginUser(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        val user = dao.getUserByEmail(email)
        if (user != null && user.passwordHash == password) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password."))
        }
    }

    suspend fun resetPassword(email: String, newPassword: String, securityAnswer: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = dao.getUserByEmail(email)
        if (user == null) {
            return@withContext Result.failure(Exception("Account not found with this email."))
        }
        if (!user.securityAnswer.equals(securityAnswer.trim(), ignoreCase = true)) {
            return@withContext Result.failure(Exception("Security recovery answer did not match."))
        }
        val updatedUser = user.copy(passwordHash = newPassword)
        dao.updateUser(updatedUser)
        if (_currentUser.value?.id == user.id) {
            _currentUser.value = updatedUser
        }
        Result.success(Unit)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun quickDemoLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            val evansUser = User(
                id = "user_evans_01",
                email = "eaayanlude@gmail.com",
                passwordHash = "demo123",
                fullName = "Evans Ayanlude Abayomi",
                organization = "WebForge Team",
                role = "Founder & CEO | Cyber Security Science (LAUTECH)",
                securityAnswer = "webforge"
            )
            dao.insertUser(evansUser)
            _currentUser.value = evansUser
        }
    }

    // --- Projects ---
    fun getAllProjects(): Flow<List<Project>> = dao.getAllProjectsFlow()

    suspend fun getProjectById(id: String): Project? = withContext(Dispatchers.IO) {
        dao.getProjectById(id)
    }

    suspend fun createProject(
        title: String,
        keyPrefix: String,
        description: String,
        category: String,
        targetAudience: String,
        techStack: String
    ): Project = withContext(Dispatchers.IO) {
        val projectId = UUID.randomUUID().toString()
        val project = Project(
            id = projectId,
            title = title,
            keyPrefix = keyPrefix.uppercase().trim(),
            description = description,
            category = category,
            targetAudience = targetAudience,
            techStack = techStack,
            userId = _currentUser.value?.id ?: "user_default"
        )
        dao.insertProject(project)

        // Create initial default SRS Document for project
        val docId = UUID.randomUUID().toString()
        val doc = SrsDocument(
            id = docId,
            projectId = projectId,
            title = "$title SRS Document",
            version = "1.0.0",
            status = "Draft",
            author = _currentUser.value?.fullName ?: "Evans Ayanlude Abayomi",
            organization = _currentUser.value?.organization ?: "WebForge Team"
        )
        dao.insertDocument(doc)

        // Seed basic IEEE 830 skeleton sections
        val sections = listOf(
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                sectionNumber = "1.0",
                title = "Introduction",
                content = "This document sets forth the Software Requirements Specification for $title.",
                orderIndex = 1
            ),
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                sectionNumber = "1.1",
                title = "Purpose and Scope",
                content = description,
                orderIndex = 2
            ),
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                sectionNumber = "2.0",
                title = "Overall Description",
                content = "System built for $targetAudience using $techStack.",
                orderIndex = 3
            ),
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                sectionNumber = "3.0",
                title = "Functional Requirements",
                content = "See System Requirements Matrix in Section 4.0.",
                orderIndex = 4
            ),
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                sectionNumber = "4.0",
                title = "Non-Functional & Security Attributes",
                content = "Availability: 99.9% uptime. Data at rest shall be encrypted with AES-256.",
                orderIndex = 5
            )
        )
        dao.insertSections(sections)

        refreshDataStoreCache()
        project
    }

    suspend fun createProjectFromTemplate(
        preset: TemplatePreset,
        customTitle: String? = null,
        customPrefix: String? = null
    ): Project = withContext(Dispatchers.IO) {
        val projectId = UUID.randomUUID().toString()
        val title = customTitle ?: preset.name
        val prefix = customPrefix ?: preset.keyPrefix

        val project = Project(
            id = projectId,
            title = title,
            keyPrefix = prefix,
            description = preset.description,
            category = preset.category,
            userId = _currentUser.value?.id ?: "user_default"
        )
        dao.insertProject(project)

        val docId = UUID.randomUUID().toString()
        val doc = SrsDocument(
            id = docId,
            projectId = projectId,
            title = "$title SRS",
            version = "1.0.0",
            status = "Draft",
            author = _currentUser.value?.fullName ?: "Evans Ayanlude Abayomi",
            organization = _currentUser.value?.organization ?: "WebForge Team"
        )
        dao.insertDocument(doc)

        // Insert sections
        val sections = preset.defaultSections.mapIndexed { idx, s ->
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                sectionNumber = s.sectionNumber,
                title = s.title,
                content = s.content,
                guidancePrompt = s.guidancePrompt,
                orderIndex = idx + 1
            )
        }
        dao.insertSections(sections)

        // Insert requirements
        val reqs = preset.defaultRequirements.mapIndexed { idx, r ->
            Requirement(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                reqId = r.reqId,
                title = r.title,
                statement = r.statement,
                type = r.type,
                priority = r.priority,
                status = "Draft",
                acceptanceCriteria = r.acceptanceCriteria,
                verificationMethod = r.verificationMethod,
                linkedUseCases = r.linkedUseCases,
                orderIndex = idx + 1
            )
        }
        dao.insertRequirements(reqs)

        // Insert use cases
        val useCases = preset.defaultUseCases.map { u ->
            UseCase(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                useCaseId = u.useCaseId,
                title = u.title,
                primaryActor = u.primaryActor,
                preConditions = u.preConditions,
                mainSuccessScenario = u.mainSuccessScenario,
                alternateFlows = u.alternateFlows,
                postConditions = u.postConditions,
                linkedRequirementIds = u.linkedRequirementIds
            )
        }
        dao.insertUseCases(useCases)

        // Create baseline revision
        val revision = DocumentRevision(
            id = UUID.randomUUID().toString(),
            documentId = docId,
            versionTag = "v1.0.0",
            summary = "Initial specification template generated from ${preset.name}",
            author = doc.author,
            snapshotSectionsCount = sections.size,
            snapshotRequirementsCount = reqs.size
        )
        dao.insertRevision(revision)

        refreshDataStoreCache()
        project
    }

    suspend fun duplicateProject(originalProject: Project): Project = withContext(Dispatchers.IO) {
        val newProjectId = UUID.randomUUID().toString()
        val clonedProject = originalProject.copy(
            id = newProjectId,
            title = "${originalProject.title} (Copy)",
            keyPrefix = "${originalProject.keyPrefix}C",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        dao.insertProject(clonedProject)

        val originalDocs = dao.getDocumentsByProjectFlow(originalProject.id).first()
        for (origDoc in originalDocs) {
            val newDocId = UUID.randomUUID().toString()
            val clonedDoc = origDoc.copy(
                id = newDocId,
                projectId = newProjectId,
                title = "${origDoc.title} (Copy)",
                lastModified = System.currentTimeMillis()
            )
            dao.insertDocument(clonedDoc)

            val origSections = dao.getSectionsByDocument(origDoc.id)
            val clonedSections = origSections.map {
                it.copy(id = UUID.randomUUID().toString(), documentId = newDocId)
            }
            dao.insertSections(clonedSections)

            val origReqs = dao.getRequirementsByDocument(origDoc.id)
            val clonedReqs = origReqs.map {
                it.copy(id = UUID.randomUUID().toString(), documentId = newDocId)
            }
            dao.insertRequirements(clonedReqs)

            val origUseCases = dao.getUseCasesByDocument(origDoc.id)
            val clonedUseCases = origUseCases.map {
                it.copy(id = UUID.randomUUID().toString(), documentId = newDocId)
            }
            dao.insertUseCases(clonedUseCases)
        }
        refreshDataStoreCache()
        clonedProject
    }

    suspend fun updateProject(project: Project) = withContext(Dispatchers.IO) {
        dao.updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProject(project: Project) = withContext(Dispatchers.IO) {
        dao.deleteProject(project)
        refreshDataStoreCache()
    }

    // --- SRS Documents ---
    fun getDocumentsByProject(projectId: String): Flow<List<SrsDocument>> = dao.getDocumentsByProjectFlow(projectId)

    fun getAllDocuments(): Flow<List<SrsDocument>> = dao.getAllDocumentsFlow()

    suspend fun getDocumentById(id: String): SrsDocument? = withContext(Dispatchers.IO) {
        dao.getDocumentById(id)
    }

    suspend fun updateDocument(document: SrsDocument) = withContext(Dispatchers.IO) {
        _isSaving.value = true
        dao.updateDocument(document.copy(lastModified = System.currentTimeMillis()))
        if (!networkMonitor.isOnline.value) {
            dataStoreCache.queuePendingMutation("UPDATE_DOC", document.id, document.title)
        } else {
            refreshDataStoreCache()
        }
        _lastSaveTime.value = System.currentTimeMillis()
        _isSaving.value = false
    }

    // --- Sections ---
    fun getSectionsByDocument(documentId: String): Flow<List<DocumentSection>> = dao.getSectionsByDocumentFlow(documentId)

    suspend fun saveSection(section: DocumentSection) = withContext(Dispatchers.IO) {
        _isSaving.value = true
        dao.insertSection(section)
        dao.getDocumentById(section.documentId)?.let { doc ->
            dao.updateDocument(doc.copy(lastModified = System.currentTimeMillis()))
        }
        if (!networkMonitor.isOnline.value) {
            dataStoreCache.queuePendingMutation("SAVE_SECTION", section.id, section.title)
        } else {
            refreshDataStoreCache()
        }
        _lastSaveTime.value = System.currentTimeMillis()
        _isSaving.value = false
    }

    suspend fun updateSectionsOrder(sections: List<DocumentSection>) = withContext(Dispatchers.IO) {
        _isSaving.value = true
        dao.insertSections(sections)
        refreshDataStoreCache()
        _lastSaveTime.value = System.currentTimeMillis()
        _isSaving.value = false
    }

    suspend fun deleteSection(section: DocumentSection) = withContext(Dispatchers.IO) {
        dao.deleteSection(section)
        if (!networkMonitor.isOnline.value) {
            dataStoreCache.queuePendingMutation("DELETE_SECTION", section.id, section.title)
        } else {
            refreshDataStoreCache()
        }
    }

    // --- Requirements ---
    fun getRequirementsByDocument(documentId: String): Flow<List<Requirement>> = dao.getRequirementsByDocumentFlow(documentId)

    suspend fun saveRequirement(requirement: Requirement) = withContext(Dispatchers.IO) {
        _isSaving.value = true
        dao.insertRequirement(requirement)
        dao.getDocumentById(requirement.documentId)?.let { doc ->
            dao.updateDocument(doc.copy(lastModified = System.currentTimeMillis()))
        }
        if (!networkMonitor.isOnline.value) {
            dataStoreCache.queuePendingMutation("SAVE_REQ", requirement.id, requirement.reqId)
        } else {
            refreshDataStoreCache()
        }
        _lastSaveTime.value = System.currentTimeMillis()
        _isSaving.value = false
    }

    suspend fun deleteRequirement(requirement: Requirement) = withContext(Dispatchers.IO) {
        dao.deleteRequirement(requirement)
        if (!networkMonitor.isOnline.value) {
            dataStoreCache.queuePendingMutation("DELETE_REQ", requirement.id, requirement.reqId)
        } else {
            refreshDataStoreCache()
        }
    }

    suspend fun generateNextRequirementId(documentId: String, projectKey: String, isFunctional: Boolean): String = withContext(Dispatchers.IO) {
        val existing = dao.getRequirementsByDocument(documentId)
        val prefix = if (isFunctional) "$projectKey-F-" else "$projectKey-NF-"
        val existingNumbers = existing
            .map { it.reqId }
            .filter { it.startsWith(prefix) }
            .mapNotNull { it.removePrefix(prefix).toIntOrNull() }

        val nextNumber = (existingNumbers.maxOrNull() ?: 0) + 1
        String.format("%s%03d", prefix, nextNumber)
    }

    // --- Use Cases ---
    fun getUseCasesByDocument(documentId: String): Flow<List<UseCase>> = dao.getUseCasesByDocumentFlow(documentId)

    suspend fun saveUseCase(useCase: UseCase) = withContext(Dispatchers.IO) {
        dao.insertUseCase(useCase)
    }

    suspend fun deleteUseCase(useCase: UseCase) = withContext(Dispatchers.IO) {
        dao.deleteUseCase(useCase)
    }

    // --- Revisions & History ---
    fun getRevisionsByDocument(documentId: String): Flow<List<DocumentRevision>> = dao.getRevisionsByDocumentFlow(documentId)

    suspend fun createRevisionSnapshot(
        documentId: String,
        versionTag: String,
        summary: String,
        author: String
    ): DocumentRevision = withContext(Dispatchers.IO) {
        val sections = dao.getSectionsByDocument(documentId)
        val reqs = dao.getRequirementsByDocument(documentId)
        val revision = DocumentRevision(
            id = UUID.randomUUID().toString(),
            documentId = documentId,
            versionTag = versionTag,
            summary = summary,
            author = author,
            snapshotSectionsCount = sections.size,
            snapshotRequirementsCount = reqs.size
        )
        dao.insertRevision(revision)

        // Update document's version
        dao.getDocumentById(documentId)?.let { doc ->
            dao.updateDocument(doc.copy(version = versionTag.removePrefix("v")))
        }

        refreshDataStoreCache()
        revision
    }

    // --- Validation ---
    suspend fun runDocumentValidation(documentId: String): ValidationReport = withContext(Dispatchers.IO) {
        val sections = dao.getSectionsByDocument(documentId)
        val reqs = dao.getRequirementsByDocument(documentId)
        val useCases = dao.getUseCasesByDocument(documentId)
        SrsValidator.evaluateDocument(sections, reqs, useCases)
    }

    // --- Export Operations ---
    suspend fun exportPdf(documentId: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val document = dao.getDocumentById(documentId) ?: throw Exception("Document not found")
            val project = dao.getProjectById(document.projectId) ?: throw Exception("Project not found")
            val sections = dao.getSectionsByDocument(documentId)
            val reqs = dao.getRequirementsByDocument(documentId)
            val useCases = dao.getUseCasesByDocument(documentId)

            val file = PdfExporter.generatePdf(context, project, document, sections, reqs, useCases)
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportDocx(documentId: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val document = dao.getDocumentById(documentId) ?: throw Exception("Document not found")
            val project = dao.getProjectById(document.projectId) ?: throw Exception("Project not found")
            val sections = dao.getSectionsByDocument(documentId)
            val reqs = dao.getRequirementsByDocument(documentId)
            val useCases = dao.getUseCasesByDocument(documentId)

            val file = DocxExporter.generateDocx(context, project, document, sections, reqs, useCases)
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun openOrShareFile(file: File, mimeType: String, isShare: Boolean = false) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = if (isShare) {
                Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

            val chooser = Intent.createChooser(intent, if (isShare) "Share Document" else "Open Document")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
