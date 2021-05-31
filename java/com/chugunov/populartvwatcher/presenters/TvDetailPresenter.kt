package com.chugunov.populartvwatcher.presenters

import com.chugunov.populartvwatcher.retrofit.api.TvApi
import com.chugunov.populartvwatcher.application.Constant
import com.chugunov.populartvwatcher.db.dao.TvDetailDao
import com.chugunov.populartvwatcher.db.dao.TvFavoriteDao
import com.chugunov.populartvwatcher.db.entities.TvDetailEntity
import com.chugunov.populartvwatcher.views.TvDetailView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import java.lang.Exception

@InjectViewState
class TvDetailPresenter(
    private val tvDetailDao: TvDetailDao,
    private val tvFavoriteDao: TvFavoriteDao,
    private val tvApi: TvApi
) : MvpPresenter<TvDetailView>() {


    //текущий id телесериала
    var id: Int? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        update()
    }


    //useCache - учитывать ли данные в БД
    fun update(useCache: Boolean = true){
        viewState.showLoading()

        if(id!=null) {

            val query = Single.create<TvDetailEntity>{response->

                var dbResponse : TvDetailEntity? = null

                if(useCache)
                    dbResponse= tvDetailDao.selectById(id!!)
                else
                    tvDetailDao.clearById(id!!)

                if(dbResponse==null){
                    tvApi.getTvDetails(id!!, Constant.API_KEY, Constant.API_LANGUAGE)
                        .subscribe ({
                            if(it.id!=null) {
                                tvDetailDao.insert(it.toTvDetailEntity())

                                //если сериал в списке избранных, обновляем в нём данные
                                val favoriteEntity = tvFavoriteDao.selectById(it.id!!)

                                if(favoriteEntity!=null){
                                    val newEntity = it.toTvFavoriteEntity()
                                    newEntity?.addedId = favoriteEntity.addedId

                                    if (newEntity != null)
                                        tvFavoriteDao.insert(newEntity)
                                }

                                response.onSuccess(it.toTvDetailEntity())
                            }
                            else
                                response.onError(Exception())
                        }, {
                            response.onError(Exception())
                        })
                }
                else
                    response.onSuccess(dbResponse)
            }

            query
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.showSuccess(it)
                },{
                    viewState.showError()
                })


            }
        }


}