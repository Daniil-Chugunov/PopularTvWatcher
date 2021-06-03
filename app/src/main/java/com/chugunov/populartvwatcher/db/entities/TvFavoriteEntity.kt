package com.chugunov.populartvwatcher.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TvFavoriteEntity (

    var id: Int,//из api
    var backdrop_path: String?,
    var name: String?,
    var first_air_date: String?,

    @PrimaryKey(autoGenerate = true)
    var addedId: Int = 0 //для соблюдения порядка вывода
){

    fun toTvListEntity() : TvListEntity{
        return TvListEntity(id, backdrop_path, name, first_air_date, -1, true)
    }

}