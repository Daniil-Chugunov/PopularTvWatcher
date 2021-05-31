package com.chugunov.populartvwatcher.retrofit.pojo

import com.chugunov.populartvwatcher.db.entities.TvDetailEntity
import com.chugunov.populartvwatcher.db.entities.TvFavoriteEntity

data class TvDetailResponse (
        var id: Int?,
        var backdrop_path: String?,
        var name: String?,
        var first_air_date: String?,
        var last_air_date: String?,
        var number_of_seasons: Int?,
        var number_of_episodes: Int?,
        var original_language: String?,
        var original_name: String?,
        var overview: String?,
        var genres : List<GenreResponse?>?
){



        fun toTvDetailEntity() : TvDetailEntity{


                //собираем все жанры в одну строку
                var concatGenres: String? = null

                if(genres?.size !=0){

                        concatGenres = ""

                        for(genre in genres!!.indices) {

                                concatGenres = concatGenres!!.plus(genres!![genre]?.name)

                                if(genre!=genres!!.size-1)
                                        concatGenres = concatGenres.plus(", ")
                        }
                }

                return TvDetailEntity(
                        id!!,
                        backdrop_path,
                        name,
                        first_air_date,
                        last_air_date,
                        number_of_seasons,
                        number_of_episodes,
                        original_language,
                        original_name,
                        overview,
                        concatGenres
                )
        }

        fun toTvFavoriteEntity(): TvFavoriteEntity?{
                if(id==null)
                        return null

                return TvFavoriteEntity(id!!, backdrop_path, name, first_air_date)
        }



}