package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val organization: String = "SpecForge Labs",
    val role: String = "Lead Software Engineer",
    val securityAnswer: String = "specforge",
    val createdAt: Long = System.currentTimeMillis()
)
