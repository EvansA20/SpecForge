package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "document_revisions",
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
data class DocumentRevision(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val versionTag: String, // e.g. "v1.0.0", "v1.1.0"
    val summary: String,
    val author: String,
    val timestamp: Long = System.currentTimeMillis(),
    val snapshotSectionsCount: Int = 0,
    val snapshotRequirementsCount: Int = 0,
    val fullSnapshotJson: String = ""
)
