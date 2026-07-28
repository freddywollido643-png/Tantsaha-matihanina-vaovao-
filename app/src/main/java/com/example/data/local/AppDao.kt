package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccineDao {
    @Query("SELECT * FROM vaccine_schedules ORDER BY scheduledDate ASC")
    fun getAllVaccineSchedules(): Flow<List<VaccineScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: VaccineScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: VaccineScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: VaccineScheduleEntity)

    @Query("UPDATE vaccine_schedules SET status = :status WHERE id = :id")
    suspend fun updateScheduleStatus(id: Long, status: String)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY savedTimestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE bookId = :bookId)")
    fun isBookmarked(bookId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE bookId = :bookId")
    suspend fun deleteBookmarkById(bookId: String)
}

@Dao
interface FarmerNoteDao {
    @Query("SELECT * FROM farmer_notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<FarmerNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: FarmerNoteEntity): Long

    @Delete
    suspend fun deleteNote(note: FarmerNoteEntity)
}
