package com.oyetech.wallpaperList.ui.wallpaperList.adapter

import com.oyetech.base.BaseHolder
import com.oyetech.extension.hide
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.materialViews.BR
import com.oyetech.materialViews.old.helper.glideHelper.setImageUrlToView
import com.oyetech.models.wallpaperModels.responses.WallpaperResponseData
import com.oyetech.wallpaperList.databinding.ItemWallpaperListViewHolderBinding

/**
Created by Erdi Özbek
-7.02.2024-
-19:44-
 **/

class WallpaperPropertyViewHolder(binding: ItemWallpaperListViewHolderBinding) :
    BaseHolder<WallpaperResponseData, ItemWallpaperListViewHolderBinding>(binding) {
    override fun bindingVariable(): Int {
        return BR.itemModel
    }

    override fun bind() {
        var rowItem = getRowItem()
        var rowBinding = getRowBinding()
        rowBinding.ivWallpaperThumb.setImageUrlToView(rowItem?.thumbs?.large)

        if (rowItem?.views == null || rowItem.views == 0) {
            rowBinding.llViewsRoot.hide()
        } else {
            rowBinding.tvViewsDesc.text = WallpaperLanguage.VIEW
            rowBinding.tvViews.text = rowItem.views.toString()
        }


        if (rowItem?.file_size == null || rowItem.file_size == 0L) {
            rowBinding.llSizeRoot.hide()
        } else {
            rowBinding.tvSizeDesc.text = WallpaperLanguage.FILE_SIZE
            rowBinding.tvSize.text = rowItem.getFileSizeString()
        }

    }
}