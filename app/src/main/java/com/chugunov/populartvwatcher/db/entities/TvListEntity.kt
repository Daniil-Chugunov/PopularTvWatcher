package com.chugunov.populartvwatcher.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TvListEntity (

    @PrimaryKey(autoGenerate = false)
    var id: Int, //из api
    var backdrop_path: String?,
    var name: String?,
    var first_air_date: String?,
    var responseIndex: Int, //для соблюдения порядка вывода
    var isFavorite: Boolean = false //для рейтинг бара
){

    fun toTvFavoriteEntity() : TvFavoriteEntity{
        return TvFavoriteEntity(id, backdrop_path, name, first_air_date)
    }


}