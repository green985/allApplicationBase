package com.oyetech.wallpaperList.ui.categoryList;

import android.view.View
import com.oyetech.base.BaseFragment
import com.oyetech.cripto.privateKeys.ads.WallpaperAppAdsKey
import com.oyetech.helper.interfaces.RecyclerViewItemClickListener
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.materialViews.helper.viewHelpers.toolbarHelper.SimpleToolbarHelperLayout
import com.oyetech.models.wallpaperModels.helperModels.WallpaperCategoryProperty
import com.oyetech.wallpaperList.R
import com.oyetech.wallpaperList.databinding.FragmentCategoryListBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-28.02.2024-
-12:16-
 **/

class CategoryListFragment : BaseFragment<FragmentCategoryListBinding, CategoryListVM>(),
    RecyclerViewItemClickListener<WallpaperCategoryProperty> {

    companion object {
        fun newInstance() = CategoryListFragment()
    }

    override val viewModel: CategoryListVM by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_category_list

    lateinit var adapter: CategoryListAdapter

    override fun prepareView() {
        SimpleToolbarHelperLayout.changeToolbarTitleCenter(
            binding.includeToolbar,
            title = WallpaperLanguage.CATEGORY_LIST,
            center = true,
            backButtonEnable = false
        )
        prepareAdapter()
    }

    override fun prepareObserver() {

    }

    override fun initAdView() {
        val adViewLiveData =
            viewModel.getAdsViewListWithIdLiveData(WallpaperAppAdsKey.BANNER_MAIN_ID)

        adViewLiveData.removeObservers(viewLifecycleOwner)
        adViewLiveData.observe(viewLifecycleOwner) {
            Timber.d("getAdsViewListWithIdLiveData == ")
            val customAdView = binding.csAdView
            customAdView.initAdView(it.firstOrNull())
        }
    }

    fun prepareAdapter() {
        adapter = CategoryListAdapter(this)
        binding.rvCategoryList.disableSwipeAction()
        binding.rvCategoryList.initRecyclerViewAdapter(adapter)

        viewModel.categoryListLiveData.observe(viewLifecycleOwner) {
            binding.rvCategoryList.submitItemToAdapter(it)
        }
    }

    override fun itemClickListener(
        adapterPosition: Int,
        itemData: WallpaperCategoryProperty?,
        view: View?,
    ) {
        viewModel.navigateWallpaperListWithTagItem(itemData)
    }

}