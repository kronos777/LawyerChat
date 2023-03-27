package com.example.lawyerapplication.di

import android.content.Context
import androidx.room.Room
import com.example.lawyerapplication.db.ChatUserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named


@Module
@InstallIn(SingletonComponent::class)
class TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDb(@ApplicationContext context: Context): ChatUserDatabase {
        return Room.inMemoryDatabaseBuilder(
             context,
             ChatUserDatabase::class.java
         ).allowMainThreadQueries().build()
    }
}