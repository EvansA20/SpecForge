package com.example.data.validator

import com.example.data.model.DocumentSection
import com.example.data.model.Requirement
import com.example.data.model.UseCase
import com.example.data.model.ValidationIssue
import com.example.data.model.ValidationReport
import com.example.data.model.ValidationSeverity
import java.util.UUID

object SrsValidator {

    private val VAGUE_TERMS = listOf(
        "user-friendly" to "Replace with measurable usability criteria (e.g., 'A novice user completes flow in < 60 seconds with 0 errors').",
        "fast" to "Specify measurable latency bounds (e.g., 'within 200ms for 95% of queries').",
        "efficient" to "Define specific resource thresholds (e.g., 'consumes less than 128MB RAM at peak').",
        "easy" to "State objective usability metrics or user qualification level.",
        "robust" to "Define specific error recovery behavior (e.g., 'reconnects within 3 seconds of network loss').",
        "flexible" to "Specify the exact configuration options or extension points supported.",
        "seamless" to "Detail the background handoff or session preservation mechanism.",
        "approximately" to "Specify exact tolerances (e.g., '± 2.5% deviation allowed').",
        "etc." to "Enumerate all valid conditions exhaustively; do not use 'etc.'.",
        "as appropriate" to "Define the exact decision rule or system criteria.",
        "if possible" to "Requirements must be testable commitments; use priority 'Could Have' instead of ambiguous phrasing."
    )

    fun evaluateDocument(
        sections: List<DocumentSection>,
        requirements: List<Requirement>,
        useCases: List<UseCase>
    ): ValidationReport {
        val issues = mutableListOf<ValidationIssue>()

        // 1. Missing Standard IEEE 830 Sections Check
        val sectionTitles = sections.map { it.title.lowercase() }
        val hasIntro = sectionTitles.any { it.contains("intro") || it.contains("purpose") }
        val hasScope = sectionTitles.any { it.contains("scope") }
        val hasOverall = sectionTitles.any { it.contains("overall") || it.contains("description") || it.contains("perspective") }
        val hasSecurityOrNfr = sectionTitles.any { it.contains("non-functional") || it.contains("security") || it.contains("performance") }
        val hasVerification = sectionTitles.any { it.contains("verification") || it.contains("acceptance") || it.contains("test") }

        if (!hasIntro) {
            issues.add(
                ValidationIssue(
                    id = UUID.randomUUID().toString(),
                    severity = ValidationSeverity.ERROR,
                    category = "Missing Mandatory Section",
                    title = "Missing Introduction Section",
                    description = "IEEE 830 standard mandates a clear Introduction section establishing project rationale.",
                    suggestion = "Add a section titled '1.0 Introduction' with project purpose and background."
                )
            )
        }

        if (!hasScope) {
            issues.add(
                ValidationIssue(
                    id = UUID.randomUUID().toString(),
                    severity = ValidationSeverity.WARNING,
                    category = "Missing Mandatory Section",
                    title = "Missing Project Scope Section",
                    description = "Defining boundaries (in-scope vs out-of-scope) prevents project creep.",
                    suggestion = "Add a section titled '1.1 Scope' explicitly identifying deliverable boundaries."
                )
            )
        }

        if (!hasOverall) {
            issues.add(
                ValidationIssue(
                    id = UUID.randomUUID().toString(),
                    severity = ValidationSeverity.WARNING,
                    category = "Missing Mandatory Section",
                    title = "Missing Overall Description",
                    description = "Target audience, operating environment, and system perspective are missing.",
                    suggestion = "Add '2.0 Overall Description' defining user roles and constraints."
                )
            )
        }

        if (!hasSecurityOrNfr) {
            issues.add(
                ValidationIssue(
                    id = UUID.randomUUID().toString(),
                    severity = ValidationSeverity.WARNING,
                    category = "Quality Attribute Gap",
                    title = "No Non-Functional / Security Section Found",
                    description = "Modern software specifications must specify security and availability targets.",
                    suggestion = "Add a section detailing security, latency, and uptime requirements."
                )
            )
        }

        // 2. Empty or Placeholder Sections Check
        sections.forEach { section ->
            if (section.content.trim().length < 30) {
                issues.add(
                    ValidationIssue(
                        id = UUID.randomUUID().toString(),
                        severity = ValidationSeverity.WARNING,
                        category = "Incomplete Section",
                        title = "Sparse Content in Section ${section.sectionNumber}",
                        description = "Section '${section.title}' has fewer than 30 characters and appears placeholder.",
                        suggestion = "Flesh out this section with detailed operational specifications.",
                        sectionNumber = section.sectionNumber
                    )
                )
            }
        }

        // 3. Duplicate Requirement IDs
        val reqIdCounts = requirements.groupBy { it.reqId.trim().uppercase() }
        reqIdCounts.filter { it.value.size > 1 }.forEach { (duplicateId, list) ->
            issues.add(
                ValidationIssue(
                    id = UUID.randomUUID().toString(),
                    severity = ValidationSeverity.ERROR,
                    category = "Duplicate Identifier",
                    title = "Duplicate Requirement ID: $duplicateId",
                    description = "ID $duplicateId is assigned to ${list.size} different requirements.",
                    suggestion = "Renumber duplicates to guarantee unique traceability.",
                    requirementId = duplicateId
                )
            )
        }

        // 4. Requirements Missing Statements or IEEE Formats
        requirements.forEach { req ->
            val statement = req.statement.trim()
            if (statement.isBlank()) {
                issues.add(
                    ValidationIssue(
                        id = UUID.randomUUID().toString(),
                        severity = ValidationSeverity.ERROR,
                        category = "Empty Statement",
                        title = "Empty Requirement Statement for ${req.reqId}",
                        description = "Requirement has a title but no specification statement.",
                        suggestion = "Define: 'The system shall [behavior] under [condition]'.",
                        requirementId = req.reqId
                    )
                )
            } else {
                if (!statement.lowercase().contains("the system shall") &&
                    !statement.lowercase().contains("the application shall")
                ) {
                    issues.add(
                        ValidationIssue(
                            id = UUID.randomUUID().toString(),
                            severity = ValidationSeverity.INFO,
                            category = "IEEE Format Convention",
                            title = "Non-Standard Statement Syntax in ${req.reqId}",
                            description = "Best practice requirement syntax starts with 'The system shall...'",
                            suggestion = "Restructure: 'The system shall ${statement.replaceFirstChar { it.lowercase() }}'.",
                            requirementId = req.reqId
                        )
                    )
                }

                // Vague words check
                VAGUE_TERMS.forEach { (vagueWord, tip) ->
                    val regex = "\\b$vagueWord\\b".toRegex(RegexOption.IGNORE_CASE)
                    if (regex.containsMatchIn(statement)) {
                        issues.add(
                            ValidationIssue(
                                id = UUID.randomUUID().toString(),
                                severity = ValidationSeverity.WARNING,
                                category = "Ambiguous Language",
                                title = "Vague Term '$vagueWord' in ${req.reqId}",
                                description = "The term '$vagueWord' is untestable in formal acceptance.",
                                suggestion = tip,
                                requirementId = req.reqId
                            )
                        )
                    }
                }
            }

            // 5. Missing Acceptance Criteria Check
            if (req.acceptanceCriteria.trim().length < 15) {
                issues.add(
                    ValidationIssue(
                        id = UUID.randomUUID().toString(),
                        severity = ValidationSeverity.WARNING,
                        category = "Missing Acceptance Criteria",
                        title = "No Acceptance Criteria in ${req.reqId}",
                        description = "QA and engineers cannot objectively verify this requirement without criteria.",
                        suggestion = "Provide a testable Given-When-Then condition or numeric metric.",
                        requirementId = req.reqId
                    )
                )
            }

            // 6. Traceability Gap Check
            if (req.type.equals("Functional", ignoreCase = true) && req.linkedUseCases.isBlank() && useCases.isNotEmpty()) {
                issues.add(
                    ValidationIssue(
                        id = UUID.randomUUID().toString(),
                        severity = ValidationSeverity.INFO,
                        category = "Traceability Gap",
                        title = "Functional Requirement ${req.reqId} Not Linked to Use Case",
                        description = "Traceability matrix is unlinked for this functional requirement.",
                        suggestion = "Link this requirement to an existing Use Case ID.",
                        requirementId = req.reqId
                    )
                )
            }
        }

        // Calculate score
        val errorCount = issues.count { it.severity == ValidationSeverity.ERROR }
        val warningCount = issues.count { it.severity == ValidationSeverity.WARNING }
        val infoCount = issues.count { it.severity == ValidationSeverity.INFO }

        var rawScore = 100 - (errorCount * 15) - (warningCount * 6) - (infoCount * 2)
        if (requirements.isEmpty()) rawScore -= 25
        if (sections.isEmpty()) rawScore -= 30

        val finalScore = rawScore.coerceIn(0, 100)
        val grade = when {
            finalScore >= 93 -> "A+"
            finalScore >= 85 -> "A"
            finalScore >= 75 -> "B"
            finalScore >= 60 -> "C"
            else -> "Needs Work"
        }

        return ValidationReport(
            score = finalScore,
            grade = grade,
            totalIssues = issues.size,
            errorCount = errorCount,
            warningCount = warningCount,
            infoCount = infoCount,
            issues = issues
        )
    }
}
