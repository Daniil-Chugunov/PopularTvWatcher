package com.chugunov.populartvwatcher.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface TvListView : MvpView {

    @OneExecution
    fun refreshAdapter()

    @OneExecution
    fun itemChangeFavoriteStatus(id: Int, position: Int) //обновить состояние звёздочки в элементе

    @AddToEndSingle
    fun bindAdapter(isBind: Boolean)
}