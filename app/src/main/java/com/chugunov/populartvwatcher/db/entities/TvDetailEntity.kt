package com.chugunov.populartvwatcher.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TvDetailEntity(

        @PrimaryKey(autoGenerate = false)
        var id: Int,//из api

        var backdrop_path: String?,
        var name: String?,
        var first_air_date: String?,
        var last_air_date: String?,
        var number_of_seasons: Int?,
        var number_of_episodes: Int?,
        var original_language: String?,
        var original_name: String?,
        var overview: String?,
        var genre_name: String?
)