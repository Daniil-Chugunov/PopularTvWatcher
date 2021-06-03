package com.chugunov.populartvwatcher.retrofit.pojo

import com.chugunov.populartvwatcher.db.entities.TvListEntity


data class TvListResponse (

        var results: List<TvListEntity>? = null,
        var total_pages: Int?
)