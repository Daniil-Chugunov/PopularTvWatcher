package com.chugunov.populartvwatcher.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chugunov.populartvwatcher.db.entities.TvListEntity

@Dao
interface TvListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<TvListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: TvListEntity)

    @Query("SELECT * FROM TvListEntity ORDER BY responseIndex")
    fun pagingSource(): PagingSource<Int, TvListEntity>

    @Query("SELECT * FROM TvListEntity WHERE id = :id")
    fun selectById(id: Int): TvListEntity?

    @Query("DELETE FROM TvListEntity")
    fun clearAll()
}