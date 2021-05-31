package com.chugunov.populartvwatcher.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chugunov.populartvwatcher.db.entities.TvFavoriteEntity

@Dao
interface TvFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: TvFavoriteEntity)

    @Query("SELECT * FROM TvFavoriteEntity ORDER BY addedId DESC")
    fun pagingSource(): PagingSource<Int, TvFavoriteEntity>

    @Query("SELECT * FROM TvFavoriteEntity")
    fun selectAll() : List<TvFavoriteEntity>

    @Query("SELECT * FROM TvFavoriteEntity WHERE id = :id")
    fun selectById(id: Int): TvFavoriteEntity?

    @Query("DELETE FROM TvFavoriteEntity WHERE id=:id")
    fun deleteById(id: Int)

    @Query("DELETE FROM TvFavoriteEntity")
    fun deleteAll()
}