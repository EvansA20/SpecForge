package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DocumentRevision
import com.example.data.model.DocumentSection
import com.example.data.model.Project
import com.example.data.model.Requirement
import com.example.data.model.SrsDocument
import com.example.data.model.UseCase
import com.example.data.model.User

@Database(
    entities = [
        User::class,
        Project::class,
        SrsDocument::class,
        DocumentSection::class,
        Requirement::class,
        UseCase::class,
        DocumentRevision::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SpecForgeDatabase : RoomDatabase() {
    abstract fun specForgeDao(): SpecForgeDao

    companion object {
        @Volatile
        private var INSTANCE: SpecForgeDatabase? = null

        fun getInstance(context: Context): SpecForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpecForgeDatabase::class.java,
                    "specforge_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
