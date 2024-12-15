package com.oyetech.wallpaperList.ui.categoryList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.oyetech.base.BaseDifferAdapter
import com.oyetech.helper.interfaces.RecyclerViewItemClickListener
import com.oyetech.materialViews.databinding.ItemWallpaperCategoryViewHolderBinding
import com.oyetech.materialViews.old.paging.diffUtils.WallpaperAppDiffUtils
import com.oyetech.models.wallpaperModels.helperModels.WallpaperCategoryProperty

/**
Created by Erdi Özbek
-28.02.2024-
-12:35-
 **/

class CategoryListAdapter(private var listener: RecyclerViewItemClickListener<WallpaperCategoryProperty>) :
    BaseDifferAdapter<WallpaperCategoryProperty, RecyclerView.ViewHolder>(WallpaperAppDiffUtils.categoryPropertyDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return WallpaperCategoryViewHolder(
            ItemWallpaperCategoryViewHolderBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder as WallpaperCategoryViewHolder
        holder.doBindings(getItem(position))
        holder.itemView.setOnClickListener {
            listener?.itemClickListener(position, getItem(position), it)
        }
        holder.bind()
    }

}