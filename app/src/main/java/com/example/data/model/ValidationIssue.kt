package com.example.data.model

enum class ValidationSeverity {
    ERROR,
    WARNING,
    INFO
}

data class ValidationIssue(
    val id: String,
    val severity: ValidationSeverity,
    val category: String,
    val title: String,
    val description: String,
    val suggestion: String,
    val requirementId: String? = null,
    val sectionNumber: String? = null
)

data class ValidationReport(
    val score: Int, // 0 - 100
    val grade: String, // "A+", "A", "B", "C", "Needs Work"
    val totalIssues: Int,
    val errorCount: Int,
    val warningCount: Int,
    val infoCount: Int,
    val issues: List<ValidationIssue>
)
