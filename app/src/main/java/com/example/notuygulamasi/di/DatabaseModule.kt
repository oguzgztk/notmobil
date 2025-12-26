package com.example.notuygulamasi.di

import android.content.Context
import androidx.room.Room
import com.example.notuygulamasi.data.local.dao.NoteDao
import com.example.notuygulamasi.data.local.database.NotMobilDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database modülü
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotMobilDatabase {
        return Room.databaseBuilder(
            context,
            NotMobilDatabase::class.java,
            "not_mobil_database"
        )
        .allowMainThreadQueries() // Development için - production'da kaldırılmalı
        .fallbackToDestructiveMigration() // Migration sorunlarını önlemek için
        .build()
    }
    
    @Provides
    @Singleton
    fun provideNoteDao(database: NotMobilDatabase): NoteDao {
        return database.noteDao()
    }
}

