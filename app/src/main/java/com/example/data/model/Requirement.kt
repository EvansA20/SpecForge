package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "requirements",
    foreignKeys = [
        ForeignKey(
            entity = SrsDocument::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("documentId"),
        Index(value = ["documentId", "reqId"], unique = true)
    ]
)
data class Requirement(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val reqId: String, // e.g. "REQ-F-001"
    val title: String,
    val statement: String, // "The system shall [behavior] under [condition]"
    val type: String = "Functional", // Functional, Non-Functional, Security, Performance, Interface, Data
    val priority: String = "Must Have", // Must Have, Should Have, Could Have, Won't Have
    val status: String = "Draft", // Draft, In Review, Approved, Implemented, Verified
    val acceptanceCriteria: String = "",
    val verificationMethod: String = "Test", // Test, Demonstration, Inspection, Analysis
    val linkedUseCases: String = "", // Comma-separated or referenced IDs
    val orderIndex: Int = 0
)
