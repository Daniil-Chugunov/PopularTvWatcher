package com.chugunov.populartvwatcher.presenters

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import androidx.paging.rxjava3.observable
import com.chugunov.populartvwatcher.application.Constant
import com.chugunov.populartvwatcher.db.dao.TvFavoriteDao
import com.chugunov.populartvwatcher.db.dao.TvListDao
import com.chugunov.populartvwatcher.db.entities.TvFavoriteEntity
import com.chugunov.populartvwatcher.views.TvFavoriteView
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
class TvFavoritePresenter(
    private val tvListDao: TvListDao,
    private val tvFavoriteDao: TvFavoriteDao
) : MvpPresenter<TvFavoriteView>() {

    var pagingData : Observable<PagingData<TvFavoriteEntity>>

    private val pagingSourceFactory =  { tvFavoriteDao.pagingSource() }

    init{

        val pager = Pager(config = PagingConfig(

            pageSize = Constant.PAGE_SIZE,
            maxSize = Constant.PAGE_SIZE* Constant.INITIAL_LOAD_PAGE_COUNT,
            enablePlaceholders = false,
            initialLoadSize = Constant.PAGE_SIZE* Constant.INITIAL_LOAD_PAGE_COUNT),
            pagingSourceFactory = pagingSourceFactory
        )

        pagingData = pager.observable.cachedIn(presenterScope)

    }

    //для определения доступности кнопки удаления
    fun checkFavoriteCount(){

        val query = Single.create<Int>{
            val count = tvFavoriteDao.selectAll().size
            it.onSuccess(count)
        }

        query
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ count ->
                if(count>0)
                    viewState.switchButtonDelete(true)
                else
                    viewState.switchButtonDelete(false)
            }, {})

    }

    //при нажатии звёздочки на элементе, работает только на удаление
    fun changeFavoriteStatus(id: Int){

        val query = Single.create<Boolean>{

            val entity = tvFavoriteDao.selectById(id)

            if(entity!=null){

                tvFavoriteDao.deleteById(entity.id)

                val tvListEntity = tvListDao.selectById(id)

                if(tvListEntity!=null) {
                    tvListEntity.isFavorite = !tvListEntity.isFavorite
                    tvListDao.insert(tvListEntity)
                }

                it.onSuccess(true)
            }
            else
                it.onSuccess(false)
        }

        query
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({success->
                if(success)
                    checkFavoriteCount()
            },{})

    }

    //удаление всего списка избранных
    fun onDialogFragmentClosedAccept(){

        val query = Single.create<Boolean>{

            val items = tvFavoriteDao.selectAll()

            for(item in items){

                //если элемент находится в общем списке, меняем статус избранного там
                val tvListItem = tvListDao.selectById(item.id)

                if(tvListItem!=null){
                    tvListItem.isFavorite = false
                    tvListDao.insert(tvListItem)
                }
            }

            tvFavoriteDao.deleteAll()

            it.onSuccess(true)
        }


        query
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ success->
                if(success)
                    viewState.switchButtonDelete(false)
            },{})
    }


}