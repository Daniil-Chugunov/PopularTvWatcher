package com.chugunov.populartvwatcher.ui.tvList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chugunov.populartvwatcher.databinding.TvListItemBinding
import com.chugunov.populartvwatcher.db.entities.TvListEntity

class TvListAdapter(
    private val onClickItem: (id: Int, title: String) -> Unit, //нажатие на весь элемент
    private val onClickFavorite: (id: Int, position: Int) -> Unit //нажатие на звёздочку
) : PagingDataAdapter<TvListEntity, TvListAdapter.TvListViewHolder>(TvListComparator) {


    class TvListViewHolder(val binding: TvListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TvListViewHolder, position: Int) {
        val currentElement = getItem(position)

        if (currentElement != null)
            holder.binding.item = currentElement
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
                        onClickFavorite(
                            it.id,
                            holder.absoluteAdapterPosition
                        )
                    }
                } catch (e: Exception) { }
            }

            true
        }


        return holder
    }

    object TvListComparator : DiffUtil.ItemCallback<TvListEntity>() {
        override fun areItemsTheSame(oldItem: TvListEntity, newItem: TvListEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TvListEntity, newItem: TvListEntity): Boolean {
            return oldItem == newItem
        }
    }



    fun notifyItemRebind(id: Int, position: Int){

        val item = getItem(position)
        if(item!=null && item.id == id)
            notifyItemChanged(position)
    }
}