package com.oyetech.wallpaperList.ui.wallpaperList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.oyetech.base.BasePagingListAdapter
import com.oyetech.helper.interfaces.RecyclerViewItemClickListener
import com.oyetech.materialViews.BR
import com.oyetech.materialViews.old.paging.diffUtils.WallpaperAppDiffUtils
import com.oyetech.materialViews.old.viewHolders.loading.ListFooterViewHolder
import com.oyetech.models.wallpaperModels.responses.WallpaperResponseData
import com.oyetech.wallpaperList.databinding.ItemWallpaperListViewHolderBinding

/**
Created by Erdi Özbek
-7.02.2024-
-19:42-
 **/

class WallpaperListAdapter(
    private var listener: RecyclerViewItemClickListener<WallpaperResponseData>? = null,
    private var retryAction: (() -> Unit)? = null,
) :
    BasePagingListAdapter<WallpaperResponseData, ViewHolder>(WallpaperAppDiffUtils.wallpaperListDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType != FOOTER_VIEW_TYPE) {
            // will be fixed for country holder...
            return WallpaperPropertyViewHolder(
                ItemWallpaperListViewHolderBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                    parent, false
                )
            )
        } else {
            // else if (viewType == FOOTER_VIEW_TYPE) {
            return ListFooterViewHolder.create(retry = retryAction, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) != FOOTER_VIEW_TYPE) {
            holder as WallpaperPropertyViewHolder
            holder.getRowBinding().setVariable(BR.clickListener, listener)
            holder.getRowBinding()?.setVariable(BR.adapterPosition, position)
            holder.doBindings(getItem(position))
            holder.itemView.setOnClickListener {
                // listener?.itemClickListener(position, getItem(position), it)
            }
            holder.bind()
        } else {
            // else if (viewType == FOOTER_VIEW_TYPE) {
            holder as ListFooterViewHolder
            holder.bind(viewState)
        }
    }

}