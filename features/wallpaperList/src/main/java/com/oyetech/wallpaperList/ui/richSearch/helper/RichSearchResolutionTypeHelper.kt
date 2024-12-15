package com.oyetech.wallpaperList.ui.richSearch.helper

import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.oyetech.core.appUtil.ViewUtil
import com.oyetech.core.stringOperation.StringHelper
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.wallpaperList.ui.richSearch.RichSearchFragment

/**
Created by Erdi Özbek
-17.02.2024-
-18:02-
 **/

fun RichSearchFragment.prepareResolutionTypeProperty() {
    // TODO will be improved later with all resolutions

    var systemResolutionPair = ViewUtil.getScreenResolution(requireContext())

    var systemResolutionText =
        StringHelper.makeResolutionTextWithSystemProperty(systemResolutionPair)

    var cbSystemResolution = binding.cbSystemResolution

    cbSystemResolution.text =
        WallpaperLanguage.SYSTEM_RESOLUTION.plus(" (") + systemResolutionText + ")"

    var checkChangeListener = object : OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            var id = p0?.id ?: return

            when (id) {
                cbSystemResolution.id -> {
                    viewModel.resolutionTypeValue(
                        systemResolutionText = systemResolutionText,
                        isChecked = p1
                    )
                }
            }
        }

    }
    cbSystemResolution.setOnCheckedChangeListener(checkChangeListener)

    binding.csResolutionSelect.setRepositoryToView(this)

}