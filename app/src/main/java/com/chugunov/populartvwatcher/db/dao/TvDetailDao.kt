package com.chugunov.populartvwatcher.db.dao

import androidx.room.*
import com.chugunov.populartvwatcher.db.entities.TvDetailEntity

@Dao
interface TvDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: TvDetailEntity)

    @Query("SELECT * FROM TvDetailEntity WHERE id = :id")
    fun selectById(id: Int): TvDetailEntity?

    @Query("DELETE FROM TvDetailEntity")
    fun clearAll()

    @Query("DELETE FROM TvDetailEntity WHERE id=:id")
    fun clearById(id: Int)
}