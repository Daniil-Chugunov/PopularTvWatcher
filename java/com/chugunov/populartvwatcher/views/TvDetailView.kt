package com.chugunov.populartvwatcher.views

import com.chugunov.populartvwatcher.db.entities.TvDetailEntity
import moxy.MvpView
import moxy.viewstate.strategy.alias.SingleState

interface TvDetailView : MvpView {

    @SingleState
    fun showSuccess(item: TvDetailEntity)

    @SingleState
    fun showError()

    @SingleState
    fun showLoading()

}