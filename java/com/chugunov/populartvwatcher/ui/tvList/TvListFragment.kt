package com.chugunov.populartvwatcher.ui.tvList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.chugunov.populartvwatcher.ui.activities.MainActivity
import com.chugunov.populartvwatcher.R
import com.chugunov.populartvwatcher.retrofit.api.TvApi
import com.chugunov.populartvwatcher.databinding.FragmentTvListBinding
import com.chugunov.populartvwatcher.db.dao.TvDetailDao
import com.chugunov.populartvwatcher.db.dao.TvFavoriteDao
import com.chugunov.populartvwatcher.db.dao.TvListDao
import com.chugunov.populartvwatcher.presenters.TvListPresenter
import com.chugunov.populartvwatcher.ui.other.StateAdapter
import com.chugunov.populartvwatcher.views.TvListView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class TvListFragment() : MvpAppCompatFragment(), TvListView {

    private var _binding: FragmentTvListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tvApi: TvApi

    @Inject
    lateinit var tvListDao: TvListDao

    @Inject
    lateinit var tvDetailDao: TvDetailDao

    @Inject
    lateinit var tvFavoriteDao: TvFavoriteDao

    @InjectPresenter
    lateinit var presenter: TvListPresenter

    @ProvidePresenter
    fun providePresenter(): TvListPresenter? {
        return TvListPresenter(tvListDao, tvDetailDao, tvFavoriteDao,  tvApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTvListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun onItemClickListener(id: Int, title: String){
        val bundle = Bundle()
        bundle.putInt("ID", id)
        bundle.putString("TITLE", title)
        (activity as MainActivity).changeFragment(R.id.tvDetailFragment, bundle)
    }


    private var tvListAdapter: TvListAdapter = TvListAdapter ({ id, title ->
        onItemClickListener(id, title)
    },
        {  id, position->
            presenter.changeFavoriteStatus(id, position)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.pagingData.subscribe{
            tvListAdapter.submitData( lifecycle, it)
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvListRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        tvListAdapter.addLoadStateListener {
            onAdapterAddLoadStateChanged(it)
        }

        binding.tvListSwiper.setOnRefreshListener {
            refreshAdapter()
        }

        val searchObservable = Observable.create<String> {
            binding.tvListSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextChange(newText: String): Boolean {
                    it.onNext(newText)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
            })
        }

        searchObservable.debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe{
                presenter.onSearchStateChanged(it)
        }

        binding.tvListRecycler.adapter = tvListAdapter
            .withLoadStateHeaderAndFooter(
                header = StateAdapter(View.OnClickListener { tvListAdapter.retry() }),
                footer = StateAdapter(View.OnClickListener { tvListAdapter.retry() })
            )

        binding.tvListButtonRetry.setOnClickListener {
            tvListAdapter.retry()
        }
    }

    override fun onResume() {
        super.onResume()

        //чтобы при смене конфигурации не появлялась клавиатура
        binding.tvListSearch.clearFocus()
    }

    private fun onAdapterAddLoadStateChanged(loadStates: CombinedLoadStates){

        if (_binding == null)
            return

        binding.tvListSwiper.isEnabled = (loadStates.prepend !is LoadState.Loading && loadStates.prepend !is LoadState.Error)

        binding.tvListProgress.isVisible = loadStates.refresh is LoadState.Loading
        binding.tvListErrorMsg.isVisible = loadStates.refresh is LoadState.Error
        binding.tvListButtonRetry.isVisible = loadStates.refresh is LoadState.Error
        binding.tvListSwiper.isVisible = loadStates.refresh !is LoadState.Loading && loadStates.refresh !is LoadState.Error
        binding.tvListSwiper.isRefreshing = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun refreshAdapter() {
        tvListAdapter.refresh()
    }

    override fun itemChangeFavoriteStatus(id: Int, position: Int) {
        tvListAdapter.notifyItemRebind(id, position)
    }


}