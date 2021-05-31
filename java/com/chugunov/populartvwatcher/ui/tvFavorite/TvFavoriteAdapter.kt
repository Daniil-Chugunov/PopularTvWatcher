package com.chugunov.populartvwatcher.ui.tvFavorite

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chugunov.populartvwatcher.databinding.TvListItemBinding
import com.chugunov.populartvwatcher.db.entities.TvFavoriteEntity

class TvFavoriteAdapter(
    private val onClickItem: (id: Int, title: String) -> Unit, //нажатие на весь элемент
    private val onClickFavorite: (id: Int) -> Unit //нажатие на звёздочку
)
    : PagingDataAdapter<TvFavoriteEntity, TvFavoriteAdapter.TvListViewHolder>(TvListComparator) {


    class TvListViewHolder(val binding: TvListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TvListViewHolder, position: Int) {
        var currentElement = getItem(position)

        if (currentElement != null)
            holder.binding.item = currentElement.toTvListEntity()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TvListViewHolder {

        val holder =  TvListViewHolder(TvListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))


        holder.binding.tvListItemLayout.setOnClickListener{
            try {
                getItem(holder.absoluteAdapterPosition)?.let {  it.name?.let { name -> onClickItem(it.id, name) } }
            }
            catch(e: Exception){}
        }

        holder.binding.tvListRatingBar.setOnTouchListener { _, event ->

            //чтобы не было лишних срабатываний
            if(event.action == MotionEvent.ACTION_UP) {
                try {
                    getItem(holder.absoluteAdapterPosition)?.let {
                        onClickFavorite(it.id)
                    }
                } catch (e: Exception) { }
            }

            true
        }


        return holder
    }

    object TvListComparator : DiffUtil.ItemCallback<TvFavoriteEntity>() {
        override fun areItemsTheSame(oldItem: TvFavoriteEntity, newItem: TvFavoriteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TvFavoriteEntity, newItem: TvFavoriteEntity): Boolean {
            return oldItem == newItem
        }
    }

}