package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "srs_documents",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class SrsDocument(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val title: String,
    val version: String = "1.0.0",
    val status: String = "Draft", // "Draft", "In Review", "Approved", "Published"
    val author: String = "Lead Architect",
    val organization: String = "Engineering Dept",
    val overview: String = "",
    val confidentiality: String = "Confidential - Engineering Internal",
    val lastModified: Long = System.currentTimeMillis()
)
