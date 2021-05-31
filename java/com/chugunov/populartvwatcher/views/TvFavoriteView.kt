package com.chugunov.populartvwatcher.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface TvFavoriteView : MvpView {

    @AddToEndSingle
    fun switchButtonDelete(isVisible: Boolean)

}