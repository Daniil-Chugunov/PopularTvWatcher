package com.chugunov.populartvwatcher.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chugunov.populartvwatcher.db.dao.TvDetailDao
import com.chugunov.populartvwatcher.db.dao.TvFavoriteDao
import com.chugunov.populartvwatcher.db.dao.TvListDao
import com.chugunov.populartvwatcher.db.entities.TvDetailEntity
import com.chugunov.populartvwatcher.db.entities.TvFavoriteEntity
import com.chugunov.populartvwatcher.db.entities.TvListEntity

@Database(entities = [TvListEntity::class, TvDetailEntity::class, TvFavoriteEntity::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun tvListDao(): TvListDao

    abstract fun tvDetailDao(): TvDetailDao

    abstract fun tvFavoriteDao(): TvFavoriteDao
}