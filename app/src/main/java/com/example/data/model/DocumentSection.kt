package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "document_sections",
    foreignKeys = [
        ForeignKey(
            entity = SrsDocument::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("documentId")]
)
data class DocumentSection(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val sectionNumber: String, // e.g. "1.1", "2.0"
    val title: String,
    val content: String,
    val orderIndex: Int,
    val isEnabled: Boolean = true,
    val guidancePrompt: String = "",
    val parentSectionId: String? = null
)
