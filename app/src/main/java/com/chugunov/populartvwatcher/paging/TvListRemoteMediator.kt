package com.chugunov.populartvwatcher.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxRemoteMediator
import com.chugunov.populartvwatcher.retrofit.api.TvApi
import com.chugunov.populartvwatcher.application.Constant
import com.chugunov.populartvwatcher.db.dao.TvFavoriteDao
import com.chugunov.populartvwatcher.db.dao.TvListDao
import com.chugunov.populartvwatcher.db.entities.TvListEntity
import io.reactivex.rxjava3.core.Single
import java.net.UnknownHostException


@ExperimentalPagingApi
class TvListRemoteMediator(
    private val tvListDao: TvListDao,
    private val favoriteDao: TvFavoriteDao,
    private val tvApi: TvApi
) : RxRemoteMediator<Int, TvListEntity>() {

    var currentPage = 1

    var searchQuery = ""

    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, TvListEntity>
    ): Single<MediatorResult> {

        var loadPage = when (loadType) {
            LoadType.REFRESH -> {
                currentPage = 1
                currentPage
            }
            LoadType.PREPEND -> {
                return Single.just(MediatorResult.Success(true))
            }
            LoadType.APPEND -> {
                currentPage
            }
        }

        try {

            //в зависимости от того, задан ли поиск
            var response = if(searchQuery.isEmpty())
                tvApi.getPopularTvList(Constant.API_KEY, Constant.API_LANGUAGE, loadPage)
            else
                tvApi.getTvListBySearch(Constant.API_KEY, Constant.API_LANGUAGE, loadPage, searchQuery)

            return response

                .map {

                    //при перезагрузке очищаем БД
                    if (it.results != null) {
                        if (loadType == LoadType.REFRESH)
                            tvListDao.clearAll()

                        for(i in it.results!!.indices) {
                            //соблюдение порядка вывода для каждой страницы
                            it.results!![i].responseIndex = (loadPage - 1) * 20 + i

                            //для определения состояния рейтинг бара
                            val favoriteEntity = favoriteDao.selectById(it.results!![i].id)

                            if(favoriteEntity!=null)
                                it.results!![i].isFavorite = true
                        }

                        tvListDao.insertAll(it.results!!)

                        var endOfPagination = false

                        //определяем, последняя ли это страница
                        if(it.total_pages!=null && it.total_pages==currentPage)
                            endOfPagination = true

                        currentPage++

                        MediatorResult.Success(endOfPaginationReached = endOfPagination)
                    } else
                        MediatorResult.Error(Exception())

                }
                .onErrorReturn {
                    MediatorResult.Error(it)
                }
                .doOnError {
                    MediatorResult.Error(it)
                }
        }catch (e: UnknownHostException) {
            return Single.just( MediatorResult.Error(e))
        }

    }

    //при старте начинаем перезагрузку данных
    override fun initializeSingle(): Single<InitializeAction> {
        return Single.just(InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

}