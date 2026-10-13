package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey
    val id: String,
    val title: String,
    val keyPrefix: String,
    val description: String,
    val category: String = "Web SaaS",
    val targetAudience: String = "Engineering Teams & Stakeholders",
    val techStack: String = "Kotlin, Compose, PostgreSQL, Microservices",
    val status: String = "Active", // "Active", "In Review", "Planning", "Archived"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val userId: String = "user_default"
)
