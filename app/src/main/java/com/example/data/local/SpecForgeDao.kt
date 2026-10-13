package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DocumentRevision
import com.example.data.model.DocumentSection
import com.example.data.model.Project
import com.example.data.model.Requirement
import com.example.data.model.SrsDocument
import com.example.data.model.UseCase
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecForgeDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    // --- Projects ---
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjectsFlow(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): Project?

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectByIdFlow(id: String): Flow<Project?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("SELECT COUNT(*) FROM projects")
    fun getProjectsCountFlow(): Flow<Int>

    // --- SRS Documents ---
    @Query("SELECT * FROM srs_documents WHERE projectId = :projectId ORDER BY lastModified DESC")
    fun getDocumentsByProjectFlow(projectId: String): Flow<List<SrsDocument>>

    @Query("SELECT * FROM srs_documents ORDER BY lastModified DESC")
    fun getAllDocumentsFlow(): Flow<List<SrsDocument>>

    @Query("SELECT * FROM srs_documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: String): SrsDocument?

    @Query("SELECT * FROM srs_documents WHERE id = :id LIMIT 1")
    fun getDocumentByIdFlow(id: String): Flow<SrsDocument?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: SrsDocument)

    @Update
    suspend fun updateDocument(document: SrsDocument)

    @Delete
    suspend fun deleteDocument(document: SrsDocument)

    @Query("SELECT COUNT(*) FROM srs_documents")
    fun getDocumentsCountFlow(): Flow<Int>

    // --- Document Sections ---
    @Query("SELECT * FROM document_sections WHERE documentId = :documentId ORDER BY orderIndex ASC")
    fun getSectionsByDocumentFlow(documentId: String): Flow<List<DocumentSection>>

    @Query("SELECT * FROM document_sections WHERE documentId = :documentId ORDER BY orderIndex ASC")
    suspend fun getSectionsByDocument(documentId: String): List<DocumentSection>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<DocumentSection>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: DocumentSection)

    @Update
    suspend fun updateSection(section: DocumentSection)

    @Delete
    suspend fun deleteSection(section: DocumentSection)

    @Query("DELETE FROM document_sections WHERE documentId = :documentId")
    suspend fun deleteSectionsByDocument(documentId: String)

    // --- Requirements ---
    @Query("SELECT * FROM requirements WHERE documentId = :documentId ORDER BY orderIndex ASC, reqId ASC")
    fun getRequirementsByDocumentFlow(documentId: String): Flow<List<Requirement>>

    @Query("SELECT * FROM requirements WHERE documentId = :documentId ORDER BY orderIndex ASC, reqId ASC")
    suspend fun getRequirementsByDocument(documentId: String): List<Requirement>

    @Query("SELECT * FROM requirements ORDER BY id ASC")
    fun getAllRequirementsFlow(): Flow<List<Requirement>>

    @Query("SELECT * FROM requirements WHERE documentId = :documentId AND reqId = :reqId LIMIT 1")
    suspend fun getRequirementByReqId(documentId: String, reqId: String): Requirement?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirements(requirements: List<Requirement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(requirement: Requirement)

    @Update
    suspend fun updateRequirement(requirement: Requirement)

    @Delete
    suspend fun deleteRequirement(requirement: Requirement)

    @Query("DELETE FROM requirements WHERE documentId = :documentId")
    suspend fun deleteRequirementsByDocument(documentId: String)

    // --- Use Cases ---
    @Query("SELECT * FROM use_cases WHERE documentId = :documentId ORDER BY useCaseId ASC")
    fun getUseCasesByDocumentFlow(documentId: String): Flow<List<UseCase>>

    @Query("SELECT * FROM use_cases WHERE documentId = :documentId ORDER BY useCaseId ASC")
    suspend fun getUseCasesByDocument(documentId: String): List<UseCase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUseCases(useCases: List<UseCase>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUseCase(useCase: UseCase)

    @Update
    suspend fun updateUseCase(useCase: UseCase)

    @Delete
    suspend fun deleteUseCase(useCase: UseCase)

    // --- Revisions ---
    @Query("SELECT * FROM document_revisions WHERE documentId = :documentId ORDER BY timestamp DESC")
    fun getRevisionsByDocumentFlow(documentId: String): Flow<List<DocumentRevision>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(revision: DocumentRevision)

    @Query("SELECT * FROM document_revisions WHERE id = :id LIMIT 1")
    suspend fun getRevisionById(id: String): DocumentRevision?
}
