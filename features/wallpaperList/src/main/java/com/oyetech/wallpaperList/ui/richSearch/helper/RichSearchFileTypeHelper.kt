package com.oyetech.wallpaperList.ui.richSearch.helper

import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.wallpaperList.ui.richSearch.RichSearchFragment

/**
Created by Erdi Özbek
-15.02.2024-
-14:51-
 **/

fun RichSearchFragment.prepareFileTypeProperty() {
    // TODO
    var cbPng = binding.cbFilePng
    var cbJpg = binding.cbFileJpg

    cbJpg.text = WallpaperLanguage.JPG
    cbPng.text = WallpaperLanguage.PNG

    var checkChangeListener = object : OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            var id = p0?.id ?: return

            when (id) {
                cbJpg.id -> {
                    if (p1) {
                        cbPng.isChecked = false
                    }
                    viewModel.fileTypeValue(isJpg = true, isChecked = p1)
                }

                cbPng.id -> {
                    if (p1) {
                        cbJpg.isChecked = false
                    }
                    viewModel.fileTypeValue(isJpg = false, isChecked = p1)
                }
            }
        }

    }
    cbPng.setOnCheckedChangeListener(checkChangeListener)
    cbJpg.setOnCheckedChangeListener(checkChangeListener)


}