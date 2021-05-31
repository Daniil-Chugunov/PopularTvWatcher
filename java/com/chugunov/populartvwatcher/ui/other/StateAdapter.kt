package com.chugunov.populartvwatcher.ui.other

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chugunov.populartvwatcher.databinding.StatusListItemBinding


class StateAdapter(
    private val retryCallback: View.OnClickListener  //нажатие кнопки перезапуска загрузки
) : LoadStateAdapter<StateAdapter.ItemViewHolder>() {


    override fun onBindViewHolder(holder: ItemViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ItemViewHolder {

       return if (loadState is LoadState.Loading || loadState is LoadState.Error)
          ItemViewHolder(StatusListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), retryCallback)
       else error("no supported")

    }


    class ItemViewHolder(val binding: StatusListItemBinding, retryCallback: View.OnClickListener) : RecyclerView.ViewHolder(binding.root){

        init{
           binding.statusListItemRetry.setOnClickListener(retryCallback)
        }

        fun bind(loadState: LoadState){
            binding.statusLstItemErrormsg.isVisible = loadState is LoadState.Error
            binding.statusListItemRetry.isVisible = loadState is LoadState.Error
            binding.statusListItemProgress.isVisible = loadState is LoadState.Loading
        }
    }



}