package com.oyetech.wallpaperList.ui.richSearch.helper

import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.models.wallpaperModels.requestBody.CategoryTags
import com.oyetech.wallpaperList.ui.richSearch.RichSearchFragment

/**
Created by Erdi Özbek
-17.02.2024-
-16:25-
 **/

fun RichSearchFragment.prepareCategoryTypeProperty() {
    var cbCategoryGeneral = binding.cbCategoryGeneral
    var cbCategoryAnime = binding.cbCategoryAnime
    var cbCategoryPeople = binding.cbCategoryPeople

    cbCategoryGeneral.text = WallpaperLanguage.GENERAL
    cbCategoryPeople.text = WallpaperLanguage.PEOPLE
    cbCategoryAnime.text = WallpaperLanguage.ANIME

    var checkChangeListener = object : OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            var id = p0?.id ?: return

            when (id) {
                cbCategoryGeneral.id -> {
                    viewModel.categoryTypeValue(CategoryTags.GENERAL, isChecked = p1)
                }

                cbCategoryPeople.id -> {
                    viewModel.categoryTypeValue(CategoryTags.PEOPLE, isChecked = p1)

                }

                cbCategoryAnime.id -> {
                    viewModel.categoryTypeValue(CategoryTags.ANIME, isChecked = p1)

                }
            }
        }

    }
    cbCategoryAnime.setOnCheckedChangeListener(checkChangeListener)
    cbCategoryPeople.setOnCheckedChangeListener(checkChangeListener)
    cbCategoryGeneral.setOnCheckedChangeListener(checkChangeListener)


}