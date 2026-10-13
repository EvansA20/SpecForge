package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "use_cases",
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
data class UseCase(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val useCaseId: String, // e.g. "UC-001"
    val title: String,
    val primaryActor: String = "End User",
    val preConditions: String = "",
    val mainSuccessScenario: String = "",
    val alternateFlows: String = "",
    val postConditions: String = "",
    val linkedRequirementIds: String = ""
)
