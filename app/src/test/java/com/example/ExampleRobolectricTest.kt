package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.export.DocxExporter
import com.example.data.export.PdfExporter
import com.example.data.model.DocumentSection
import com.example.data.model.Project
import com.example.data.model.Requirement
import com.example.data.model.SrsDocument
import com.example.data.model.UseCase
import com.example.data.model.ValidationSeverity
import com.example.data.validator.SrsValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SpecForge", appName)
    }

    @Test
    fun `test srs validator detects vague terms and calculates score`() {
        val sections = listOf(
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = "doc1",
                sectionNumber = "1.0",
                title = "Introduction",
                content = "This document sets forth comprehensive requirements for the payment engine.",
                orderIndex = 1
            ),
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = "doc1",
                sectionNumber = "1.1",
                title = "Scope",
                content = "The scope defines the microservices, databases, and APIs.",
                orderIndex = 2
            ),
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = "doc1",
                sectionNumber = "2.0",
                title = "Overall Description",
                content = "Operating in Kubernetes with PostgreSQL 16 database storage.",
                orderIndex = 3
            ),
            DocumentSection(
                id = UUID.randomUUID().toString(),
                documentId = "doc1",
                sectionNumber = "3.0",
                title = "Security & Non-Functional Attributes",
                content = "All communications protected with TLS 1.3 and hardware-backed keys.",
                orderIndex = 4
            )
        )

        val requirements = listOf(
            Requirement(
                id = UUID.randomUUID().toString(),
                documentId = "doc1",
                reqId = "PAY-F-001",
                title = "Fast Checkout Processing",
                statement = "The system shall process checkout in a fast and user-friendly manner under high load.",
                type = "Functional",
                priority = "Must Have",
                acceptanceCriteria = "Given valid cart, when checkout is submitted, then response returns in under 200ms.",
                verificationMethod = "Test"
            )
        )

        val report = SrsValidator.evaluateDocument(sections, requirements, emptyList())
        assertNotNull(report)
        assertTrue(report.score > 0)
        // Check that vague terms "fast" and "user-friendly" were flagged as warnings
        assertTrue(report.issues.any { it.category == "Ambiguous Language" && it.title.contains("fast") })
        assertTrue(report.issues.any { it.category == "Ambiguous Language" && it.title.contains("user-friendly") })
    }

    @Test
    fun `test docx export generates non-empty valid file`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val project = Project(
            id = "proj1",
            title = "Test Payment Gateway",
            keyPrefix = "PAY",
            description = "High scale payment gateway"
        )
        val document = SrsDocument(
            id = "doc1",
            projectId = "proj1",
            title = "Payment SRS",
            version = "1.0.0"
        )
        val sections = listOf(
            DocumentSection(
                id = "sec1",
                documentId = "doc1",
                sectionNumber = "1.0",
                title = "Introduction",
                content = "Testing DOCX generation with valid OpenXML ZIP stream.",
                orderIndex = 1
            )
        )
        val requirements = listOf(
            Requirement(
                id = "req1",
                documentId = "doc1",
                reqId = "PAY-F-001",
                title = "Ledger Commit",
                statement = "The system shall commit transactions atomically under concurrent requests.",
                acceptanceCriteria = "Database confirms serializable isolation level."
            )
        )

        val docxFile = DocxExporter.generateDocx(context, project, document, sections, requirements, emptyList())
        assertTrue(docxFile.exists())
        assertTrue(docxFile.length() > 0)
    }

    @Test
    fun `test datastore srs caching and offline sync queue`() = kotlinx.coroutines.test.runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val cache = com.example.data.sync.SrsDataStoreCache(context)

        val doc = SrsDocument(
            id = "doc_cache_01",
            projectId = "proj_01",
            title = "Cached Architecture SRS",
            version = "1.0.0"
        )
        val section = DocumentSection(
            id = "sec_cache_01",
            documentId = "doc_cache_01",
            sectionNumber = "1.0",
            title = "Cached Scope",
            content = "This content was cached in DataStore for offline inspection.",
            orderIndex = 1
        )
        val req = Requirement(
            id = "req_cache_01",
            documentId = "doc_cache_01",
            reqId = "CAC-F-001",
            title = "Offline Storage",
            statement = "The system shall store cached records locally in DataStore."
        )

        // Cache snapshot
        cache.cacheSnapshot(
            documents = listOf(doc),
            sectionsMap = mapOf("doc_cache_01" to listOf(section)),
            requirementsMap = mapOf("doc_cache_01" to listOf(req))
        )

        // Read back from DataStore cache
        val cachedSections = cache.getCachedSections("doc_cache_01")
        assertEquals(1, cachedSections.size)
        assertEquals("Cached Scope", cachedSections[0].title)

        val cachedReqs = cache.getCachedRequirements("doc_cache_01")
        assertEquals(1, cachedReqs.size)
        assertEquals("CAC-F-001", cachedReqs[0].reqId)

        // Queue offline mutations
        cache.queuePendingMutation("UPDATE_SECTION", "sec_cache_01", "Updated content while offline")
        cache.queuePendingMutation("NEW_REQ", "req_cache_02", "New requirement while offline")

        // Flush mutations upon reconnect
        val flushed = cache.flushPendingMutations()
        assertEquals(2, flushed.size)
        assertEquals("UPDATE_SECTION", flushed[0].type)
        assertEquals("NEW_REQ", flushed[1].type)

        // Queue should now be empty
        val secondFlush = cache.flushPendingMutations()
        assertEquals(0, secondFlush.size)
    }
}
