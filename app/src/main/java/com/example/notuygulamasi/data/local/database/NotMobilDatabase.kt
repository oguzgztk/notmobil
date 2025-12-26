package com.example.notuygulamasi.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notuygulamasi.data.local.dao.NoteDao
import com.example.notuygulamasi.data.local.entity.NoteEntity

/**
 * Room Database
 */
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotMobilDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

