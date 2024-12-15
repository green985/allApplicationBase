package com.oyetech.wallpaperList.ui.richSearch.helper

import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.models.wallpaperModels.requestBody.PurityTags
import com.oyetech.wallpaperList.ui.richSearch.RichSearchFragment

/**
Created by Erdi Özbek
-15.02.2024-
-17:13-
 **/

fun RichSearchFragment.preparePurityTypeProperty() {
    // TODO
    var cbPuritySketchy = binding.cbPuritySketchy
    var cbPurityNsfw = binding.cbPurityNsfw
    var cbPuritySfw = binding.cbPuritySfw

    cbPurityNsfw.text = WallpaperLanguage.NFSW
    cbPuritySketchy.text = WallpaperLanguage.SKETCHY
    cbPuritySfw.text = WallpaperLanguage.SFW

    var checkChangeListener = object : OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            var id = p0?.id ?: return

            when (id) {
                cbPurityNsfw.id -> {
                    viewModel.purityTypeValueWithTag(
                        PurityTags.NSFW,
                        isChecked = p1
                    )
                }

                cbPuritySketchy.id -> {
                    viewModel.purityTypeValueWithTag(
                        PurityTags.SKETCHY,
                        isChecked = p1
                    )
                }

                cbPuritySfw.id -> {
                    viewModel.purityTypeValueWithTag(
                        PurityTags.SFW,
                        isChecked = p1
                    )
                }
            }
        }

    }
    cbPuritySketchy.setOnCheckedChangeListener(checkChangeListener)
    cbPurityNsfw.setOnCheckedChangeListener(checkChangeListener)
    cbPuritySfw.setOnCheckedChangeListener(checkChangeListener)


}