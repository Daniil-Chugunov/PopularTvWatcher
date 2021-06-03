package com.chugunov.populartvwatcher.retrofit.api

import com.chugunov.populartvwatcher.retrofit.pojo.TvDetailResponse
import com.chugunov.populartvwatcher.retrofit.pojo.TvListResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface TvApi {

    @GET("tv/popular")
    fun getPopularTvList(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: Int)
            : Single<TvListResponse>



    @GET("search/tv")
    fun getTvListBySearch(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("query") query: String)
            : Single<TvListResponse>



    @GET("tv/{id}")
    fun getTvDetails(
            @Path("id") id: Int,
            @Query("api_key") api_key: String,
            @Query("language") language: String
    ): Single<TvDetailResponse>


}