package com.chugunov.populartvwatcher.ui.other

import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chugunov.populartvwatcher.R
import com.chugunov.populartvwatcher.application.Constant
import com.squareup.picasso.Picasso

@BindingAdapter("android:src")
fun imageLoader(imageView: ImageView, backdrop_path: String?) {
    Picasso
            .get()
            .load(Constant.BASE_IMAGE_URL+backdrop_path)
            .placeholder(R.drawable.ic_photo_placeholder)
            .error(R.drawable.ic_photo_error)
            .fit()
            .centerCrop()
            .into(imageView)
}


@BindingAdapter("android:text")
fun setText(view: TextView, value: Int) {
    view.text = value.toString()
}

@BindingAdapter("android:rating")
fun setRating(view: RatingBar, value: Boolean){
    if(value)
        view.rating = 1F
    else
        view.rating = 0F
}