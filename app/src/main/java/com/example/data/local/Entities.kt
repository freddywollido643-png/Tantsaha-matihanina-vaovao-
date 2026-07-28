package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccine_schedules")
data class VaccineScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val animalType: String,
    val batchName: String,
    val vaccineName: String,
    val scheduledDate: String,
    val status: String, // "Mbola hovana", "Vita", "Lasa date"
    val animalCount: Int,
    val notes: String
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val bookId: String,
    val title: String,
    val category: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "farmer_notes")
data class FarmerNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Kajy", "Torohevitra", "General"
    val content: String,
    val dateCreated: String
)
