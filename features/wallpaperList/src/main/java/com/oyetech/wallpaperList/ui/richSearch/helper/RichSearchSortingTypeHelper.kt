package com.oyetech.wallpaperList.ui.richSearch.helper

import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.wallpaperList.ui.richSearch.RichSearchFragment

/**
Created by Erdi Özbek
-17.02.2024-
-16:45-
 **/

fun RichSearchFragment.prepareSortingTypeProperty() {
    // TODO
    var cbOrderDesc = binding.cbOrderDesc
    var cbOrderAsc = binding.cbOrderAsc

    cbOrderAsc.text = WallpaperLanguage.ASC
    cbOrderDesc.text = WallpaperLanguage.DESC

    var checkChangeListener = object : OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            var id = p0?.id ?: return

            when (id) {
                cbOrderAsc.id -> {
                    if (p1) {
                        cbOrderDesc.isChecked = false
                    }
                    viewModel.orderTypeValue(isDesc = false, isChecked = p1)
                }

                cbOrderDesc.id -> {
                    if (p1) {
                        cbOrderAsc.isChecked = false
                    }
                    viewModel.orderTypeValue(isDesc = true, isChecked = p1)
                }
            }
        }

    }
    cbOrderDesc.setOnCheckedChangeListener(checkChangeListener)
    cbOrderAsc.setOnCheckedChangeListener(checkChangeListener)


}