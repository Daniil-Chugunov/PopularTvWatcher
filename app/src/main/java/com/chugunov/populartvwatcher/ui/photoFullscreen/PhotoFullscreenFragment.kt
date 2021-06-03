package com.chugunov.populartvwatcher.ui.photoFullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chugunov.populartvwatcher.R
import com.chugunov.populartvwatcher.application.Constant
import com.chugunov.populartvwatcher.databinding.FragmentPhotoFullscreenBinding
import com.squareup.picasso.Picasso

class PhotoFullscreenFragment : Fragment() {

    private var _binding: FragmentPhotoFullscreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoFullscreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //стрелочка
        binding.photoFullscreenToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        arguments?.let{
            Picasso
                .get()
                .load(Constant.BASE_IMAGE_URL+it.getString("backdrop_path"))
                .placeholder(R.drawable.ic_photo_placeholder)
                .error(R.drawable.ic_photo_error)
                .into(binding.photoFullscreenImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}