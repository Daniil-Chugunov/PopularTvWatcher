package com.chugunov.populartvwatcher.presenters

import androidx.paging.*
import androidx.paging.rxjava3.cachedIn
import androidx.paging.rxjava3.observable
import com.chugunov.populartvwatcher.retrofit.api.TvApi
import com.chugunov.populartvwatcher.application.Constant
import com.chugunov.populartvwatcher.db.dao.TvDetailDao
import com.chugunov.populartvwatcher.db.dao.TvFavoriteDao
import com.chugunov.populartvwatcher.db.dao.TvListDao
import com.chugunov.populartvwatcher.db.entities.TvListEntity
import com.chugunov.populartvwatcher.paging.TvListRemoteMediator
import com.chugunov.populartvwatcher.views.TvListView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope


@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@InjectViewState
class TvListPresenter (
        private val tvListDao: TvListDao,
        private val tvDetailDao: TvDetailDao,
        private val favoriteDao: TvFavoriteDao,
        tvApi: TvApi
) : MvpPresenter<TvListView>() {

    var pagingData : Observable<PagingData<TvListEntity>>

    private val remoteMediator: TvListRemoteMediator = TvListRemoteMediator(tvListDao, favoriteDao, tvApi)

    private val pagingSourceFactory =  { tvListDao.pagingSource() }

    init{

        val pager = Pager(config = PagingConfig(

            pageSize = Constant.PAGE_SIZE,
            maxSize = Constant.PAGE_SIZE*Constant.INITIAL_LOAD_PAGE_COUNT,
            enablePlaceholders = false,
            initialLoadSize = Constant.PAGE_SIZE*Constant.INITIAL_LOAD_PAGE_COUNT),
            remoteMediator = remoteMediator,
            pagingSourceFactory = pagingSourceFactory
        )

        pagingData = pager.observable.cachedIn(presenterScope)
    }


    //запрос на поиске изменился
    fun onSearchStateChanged(searchQuery: String){

        //обработка случая строки, состоящей только из пробелов
        var search = searchQuery
        var noSpace = false

        for(element in search){
            if(element != ' ') {
                noSpace = true
                break
            }
        }

        if(noSpace)
            remoteMediator.searchQuery = search
        else
            remoteMediator.searchQuery = ""

        viewState.refreshAdapter()
    }

    //при нажатии звёздочки на элементе
    fun changeFavoriteStatus(id: Int, position: Int){

        val query = Single.create<Boolean>{

            val entity = tvListDao.selectById(id)

            if(entity!=null){

                //в зависимости от текущего статуса добавляем в избранные либо удаляем оттуда
                if(!entity.isFavorite)
                    favoriteDao.insert(entity.toTvFavoriteEntity())
                else
                    favoriteDao.deleteById(entity.id)

                entity.isFavorite = !entity.isFavorite

                tvListDao.insert(entity)

                it.onSuccess(true)
            }
            else
                it.onSuccess(false)
        }

        query
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it)
                    viewState.itemChangeFavoriteStatus(id, position)
            }, {})

    }

    override fun onDestroy() {
        val query = Single.create<Boolean> {
            tvDetailDao.clearAll()
            it.onSuccess(true)
        }

        query
            .subscribeOn(Schedulers.io())
            .subscribe({
                super.onDestroy()
            }, {
                super.onDestroy()
            })
    }

}