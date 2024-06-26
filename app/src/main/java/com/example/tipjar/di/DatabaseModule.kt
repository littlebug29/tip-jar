package com.example.tipjar.di

import android.content.Context
import androidx.room.Room
import com.example.tipjar.TipHistoryRepository
import com.example.tipjar.database.TipDatabase
import com.example.tipjar.database.dao.TipHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun providesTipDatabase(
        @ApplicationContext context: Context
    ): TipDatabase = Room.databaseBuilder(
        context,
        TipDatabase::class.java,
        "tip_database"
    ).build()

    @Provides
    fun providesTipHistoryDao(tipDatabase: TipDatabase) = tipDatabase.tipHistoryDao()

    @Singleton
    @Provides
    fun providesTipHistoryRepository(tipHistoryDao: TipHistoryDao) =
        TipHistoryRepository(tipHistoryDao)
}