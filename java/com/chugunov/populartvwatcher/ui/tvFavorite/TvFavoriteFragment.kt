package com.chugunov.populartvwatcher.ui.tvFavorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.chugunov.populartvwatcher.R
import com.chugunov.populartvwatcher.databinding.FragmentTvFavoriteBinding
import com.chugunov.populartvwatcher.db.dao.TvFavoriteDao
import com.chugunov.populartvwatcher.db.dao.TvListDao
import com.chugunov.populartvwatcher.presenters.TvFavoritePresenter
import com.chugunov.populartvwatcher.ui.activities.MainActivity
import com.chugunov.populartvwatcher.ui.other.StateAdapter
import com.chugunov.populartvwatcher.views.TvFavoriteView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.lang.ref.WeakReference
import javax.inject.Inject


@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class TvFavoriteFragment : MvpAppCompatFragment(), TvFavoriteView {

    private var _binding: FragmentTvFavoriteBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tvListDao: TvListDao

    @Inject
    lateinit var tvFavoriteDao: TvFavoriteDao


    @InjectPresenter
    lateinit var presenter: TvFavoritePresenter

    @ProvidePresenter
    fun providePresenter(): TvFavoritePresenter? {
        return TvFavoritePresenter(tvListDao, tvFavoriteDao)
    }


    private fun onItemClickListener(id: Int, title: String){
        val bundle = Bundle()
        bundle.putInt("ID", id)
        bundle.putString("TITLE", title)
        (activity as MainActivity).changeFragment(R.id.tvDetailFragment, bundle)
    }

    private var tvFavoriteAdapter: TvFavoriteAdapter = TvFavoriteAdapter ({ id, title ->
        onItemClickListener(id, title)
    },
        { id ->
            presenter.changeFavoriteStatus(id)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.pagingData.subscribe{
            tvFavoriteAdapter.submitData( lifecycle, it)
        }

    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTvFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }


    fun onDialogFragmentClosedAccept(){
        presenter.onDialogFragmentClosedAccept()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvFavoriteRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        tvFavoriteAdapter.addLoadStateListener {
            onAdapterAddLoadStateChanged(it)
        }

        binding.tvFavoriteSwiper.setOnRefreshListener {
            tvFavoriteAdapter.refresh()
        }

        binding.tvFavoriteRecycler.adapter = tvFavoriteAdapter
            .withLoadStateHeaderAndFooter(
                header = StateAdapter(View.OnClickListener { tvFavoriteAdapter.retry() }),
                footer = StateAdapter(View.OnClickListener { tvFavoriteAdapter.retry() })
            )

        binding.tvFavoriteButtonDeleteAll.setOnClickListener {
            val dialog = TvFavoriteDialogFragment()
            TvFavoriteDialogFragment.parent = WeakReference(this)
            dialog.show((activity as MainActivity).supportFragmentManager, "dialog")
        }

        //чтобы понять, нужно ли показывать кнопку удаления
        presenter.checkFavoriteCount()
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onAdapterAddLoadStateChanged(loadStates : CombinedLoadStates){

        if (_binding == null)
            return

        binding.tvFavoriteSwiper.isEnabled = (loadStates.prepend !is LoadState.Loading && loadStates.prepend !is LoadState.Error)

        binding.tvFavoriteProgress.isVisible = loadStates.refresh is LoadState.Loading
        binding.tvFavoriteSwiper.isVisible = loadStates.refresh !is LoadState.Loading && loadStates.refresh !is LoadState.Error
        binding.tvFavoriteSwiper.isRefreshing = false

    }

    override fun switchButtonDelete(isVisible: Boolean) {
        binding.tvFavoriteButtonDeleteAll.isVisible = isVisible
    }


}