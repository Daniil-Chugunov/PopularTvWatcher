package com.chugunov.populartvwatcher.ui.tvDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chugunov.populartvwatcher.ui.activities.MainActivity
import com.chugunov.populartvwatcher.R
import com.chugunov.populartvwatcher.retrofit.api.TvApi
import com.chugunov.populartvwatcher.databinding.FragmentTvDetailBinding
import com.chugunov.populartvwatcher.db.dao.TvDetailDao
import com.chugunov.populartvwatcher.db.dao.TvFavoriteDao
import com.chugunov.populartvwatcher.db.entities.TvDetailEntity
import com.chugunov.populartvwatcher.presenters.TvDetailPresenter
import com.chugunov.populartvwatcher.views.TvDetailView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

@AndroidEntryPoint
class TvDetailFragment : MvpAppCompatFragment(), TvDetailView {

    private var _binding: FragmentTvDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tvApi: TvApi

    @Inject
    lateinit var tvDetailDao: TvDetailDao

    @Inject
    lateinit var tvFavoriteDao: TvFavoriteDao

    @InjectPresenter
    lateinit var presenter: TvDetailPresenter

    @ProvidePresenter
    fun providePresenter(): TvDetailPresenter? {
        return TvDetailPresenter(tvDetailDao, tvFavoriteDao, tvApi)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            presenter.id = it.getInt("ID")
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTvDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let{
            binding.tvDetailToolbar.title = it.getString("TITLE")
        }

        binding.tvDetailButtonRetry.setOnClickListener {
            presenter.update()
        }

        binding.tvDetailSwiper.setOnRefreshListener {
            presenter.update(false)
        }

        //стрелочка
        binding.tvDetailToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun showSuccess(item: TvDetailEntity){
        binding.item = item

        binding.tvDetailImage.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("backdrop_path", item.backdrop_path)
            (activity as MainActivity).changeFragment(R.id.photoFullscreenFragment, bundle)
        }

        binding.tvDetailSwiper.isRefreshing = false

        binding.tvDetailProgress.visibility = View.GONE
        binding.tvDetailButtonRetry.visibility = View.GONE
        binding.tvDetailErrorMsg.visibility = View.GONE

        binding.tvDetailSwiper.visibility = View.VISIBLE

    }

    override fun showError() {
        binding.tvDetailSwiper.isRefreshing = false

        binding.tvDetailProgress.visibility = View.GONE
        binding.tvDetailSwiper.visibility = View.GONE

        binding.tvDetailButtonRetry.visibility = View.VISIBLE
        binding.tvDetailErrorMsg.visibility = View.VISIBLE
    }

    override fun showLoading() {
        binding.tvDetailSwiper.visibility = View.GONE

        binding.tvDetailButtonRetry.visibility = View.GONE
        binding.tvDetailErrorMsg.visibility = View.GONE

        binding.tvDetailProgress.visibility = View.VISIBLE
    }


}