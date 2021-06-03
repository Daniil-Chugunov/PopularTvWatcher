package com.chugunov.populartvwatcher.ui.tvFavorite

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.paging.ExperimentalPagingApi
import com.chugunov.populartvwatcher.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.ref.WeakReference

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
class TvFavoriteDialogFragment : DialogFragment() {


    companion object{
        lateinit var parent: WeakReference<TvFavoriteFragment>
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder.setMessage(context?.getString(R.string.tv_favorite_dialog_fragment_message))
                .setNegativeButton(context?.getString(R.string.dialog_fragment_negative_button_text)){
                        dialog, _ -> dialog.cancel()
                }
                .setPositiveButton(context?.getString(R.string.dialog_fragment_positive_button_text)) {
                        dialog, _ ->
                            parent.get()?.onDialogFragmentClosedAccept()
                            dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("activity null")
    }

}