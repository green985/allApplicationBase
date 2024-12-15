package com.oyetech.wallpaperList.ui.richSearch;

import android.widget.CompoundButton
import com.oyetech.base.BaseFragment
import com.oyetech.domain.useCases.wallpaperApp.WallpaperSearchOperationUseCase
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.materialViews.customViews.layouts.colorSelect.CustomColorSingleSelectView.CustomColorSingleSelectViewRepository
import com.oyetech.materialViews.customViews.layouts.ratioSelect.CustomRatioSelectView.CustomRatioSelectViewRepository
import com.oyetech.materialViews.customViews.layouts.resolutionSelect.CustomResolutionSelectView.CustomResolutionSelectViewRepository
import com.oyetech.wallpaperList.R
import com.oyetech.wallpaperList.databinding.FragmentRichSearchBinding
import com.oyetech.wallpaperList.ui.richSearch.helper.prepareCategoryTypeProperty
import com.oyetech.wallpaperList.ui.richSearch.helper.prepareColorTypeProperty
import com.oyetech.wallpaperList.ui.richSearch.helper.prepareFileTypeProperty
import com.oyetech.wallpaperList.ui.richSearch.helper.preparePurityTypeProperty
import com.oyetech.wallpaperList.ui.richSearch.helper.prepareRatioTypeProperty
import com.oyetech.wallpaperList.ui.richSearch.helper.prepareResolutionTypeProperty
import com.oyetech.wallpaperList.ui.richSearch.helper.prepareSortingTypeProperty
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
Created by Erdi Özbek
-15.02.2024-
-13:35-
 **/

class RichSearchFragment : BaseFragment<FragmentRichSearchBinding, RichSearchVM>(),
    CustomColorSingleSelectViewRepository, CustomResolutionSelectViewRepository,
    CustomRatioSelectViewRepository {

    companion object {
        fun newInstance(wallpaperSearchOperationUseCase: WallpaperSearchOperationUseCase): RichSearchFragment {
            val fragment = RichSearchFragment()
            fragment.wallpaperSearchOperationUseCase = wallpaperSearchOperationUseCase
            return fragment
        }
    }

    private lateinit var wallpaperSearchOperationUseCase: WallpaperSearchOperationUseCase
    override val viewModel: RichSearchVM by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_rich_search

    override fun prepareView() {
        viewModel.wallpaperSearchOperationUseCase = wallpaperSearchOperationUseCase
        prepareFileTypeProperty()
        preparePurityTypeProperty()
        prepareCategoryTypeProperty()
        prepareSortingTypeProperty()
        prepareColorTypeProperty()
        prepareResolutionTypeProperty()
        prepareRatioTypeProperty()
    }

    override fun setFragmentStaticViewClickAndText() {
        binding.apply {
            tvLabelCategoryName.text = WallpaperLanguage.CATEGORY
            tvLabelPurityName.text = WallpaperLanguage.PURITY
            tvLabelRatioName.text = WallpaperLanguage.RATIO
            tvLabelResolutionName.text = WallpaperLanguage.RESOLUTION
            tvLabelColorName.text = WallpaperLanguage.COLOR
            tvFileLabelName.text = WallpaperLanguage.FILE_TYPE
            tvLabelOrderName.text = WallpaperLanguage.ORDER
        }
    }

    override fun prepareObserver() {
    }

    override fun onColorSelectCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean) {
        var colorString = compoundButton?.text.toString()
        viewModel.colorTypeValueWithTag(colorString, isChecked)
    }

    override fun onResolutionSelectCheckedChanged(
        compoundButton: CompoundButton?,
        isChecked: Boolean,
    ) {
        var resolutionText = compoundButton?.text.toString()
        viewModel.resolutionTypeValue(resolutionText, isChecked)
    }

    override fun onResolutionAtLeastExactlySelectCheckedChanged(
        compoundButton: CompoundButton?,
        isChecked: Boolean,
    ) {
        var id = compoundButton?.id ?: return

        when (id) {
            com.oyetech.materialViews.R.id.cbExactly -> {
                viewModel.resolutionAtLeastExactlyTypeValue(
                    isAtLeast = false,
                    isChecked = isChecked
                )
            }

            com.oyetech.materialViews.R.id.cbAtLeast -> {
                viewModel.resolutionAtLeastExactlyTypeValue(
                    isAtLeast = true,
                    isChecked = isChecked
                )
            }
        }


    }

    override fun onRatioSelectCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean) {
        if (compoundButton == null) return

        viewModel.resolutionRatioTypeValue(
            ratio = compoundButton.text.toString(),
            isChecked = isChecked
        )

    }
}