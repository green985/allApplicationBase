package com.oyetech.wallpaperList.ui.categoryList

import com.oyetech.base.BaseHolder
import com.oyetech.materialViews.databinding.ItemWallpaperCategoryViewHolderBinding
import com.oyetech.models.wallpaperModels.helperModels.WallpaperCategoryProperty

/**
Created by Erdi Özbek
-28.02.2024-
-12:43-
 **/

class WallpaperCategoryViewHolder(binding: ItemWallpaperCategoryViewHolderBinding) :
    BaseHolder<WallpaperCategoryProperty, ItemWallpaperCategoryViewHolderBinding>(binding) {
    override fun bindingVariable(): Int {
        return 0
    }

    override fun bind() {
        var rowBinding = getRowBinding()
        var rowItem = getRowItem()

        rowItem?.let {
            rowBinding.tvCategoryName.text = rowItem.tagName
            rowBinding.tvCategoryDesc.text = rowItem.category
        }


    }

}