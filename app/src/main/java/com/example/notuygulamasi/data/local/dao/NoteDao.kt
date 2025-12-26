package com.example.notuygulamasi.data.local.dao

import androidx.room.*
import com.example.notuygulamasi.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO - Not veritabanı işlemleri
 */
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?
    
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE tags LIKE '%' || :tag || '%' ORDER BY updatedAt DESC")
    fun filterNotesByTag(tag: String): Flow<List<NoteEntity>>
    
    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<NoteEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)
    
    @Update
    suspend fun updateNote(note: NoteEntity)
    
    @Delete
    suspend fun deleteNote(note: NoteEntity)
    
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)
}

