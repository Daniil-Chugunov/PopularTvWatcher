package com.chugunov.populartvwatcher.di

import android.content.Context
import androidx.room.Room
import com.chugunov.populartvwatcher.db.ApplicationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDB(@ApplicationContext applicationContext: Context) : ApplicationDatabase =
        Room.databaseBuilder(
            applicationContext,
            ApplicationDatabase::class.java,
            applicationContext.cacheDir.toString()+"/database"
        ).build()

    @Provides
    @Singleton
    fun provideTvListDao(database : ApplicationDatabase) = database.tvListDao()

    @Provides
    @Singleton
    fun provideTvDetailDao(database : ApplicationDatabase) = database.tvDetailDao()

    @Provides
    @Singleton
    fun provideTvFavoriteDao(database : ApplicationDatabase) = database.tvFavoriteDao()


}