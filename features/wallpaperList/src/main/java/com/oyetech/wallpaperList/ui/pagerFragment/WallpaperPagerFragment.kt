package com.oyetech.wallpaperList.ui.pagerFragment;

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.oyetech.base.BaseFragment
import com.oyetech.cripto.privateKeys.ads.WallpaperAppAdsKey
import com.oyetech.materialViews.helper.viewPager.reduceDragSensitivity
import com.oyetech.wallpaperList.R
import com.oyetech.wallpaperList.databinding.FragmentWallpaperPagerBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-23.02.2024-
-15:42-
 **/

class WallpaperPagerFragment : BaseFragment<FragmentWallpaperPagerBinding, WallpaperPagerVM>() {

    companion object {
        fun newInstance() = WallpaperPagerFragment()
    }

    override val viewModel: WallpaperPagerVM by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_wallpaper_pager

    lateinit var pagerAdapter: WallpaperViewPagerAdapter

    override fun prepareView() {
        initViewPager()
    }

    override fun prepareObserver() = Unit

    private fun initViewPager() {

        val pathList = viewModel.getWallPaperListSearchKey()
        val listNameList = viewModel.getTranslationWallpaperListText()
        pagerAdapter = WallpaperViewPagerAdapter(this, pathList)
        // binding.viewPager.recyclerView.enforceSingleScrollDirection()
        // binding.viewPager.offscreenPageLimit = pageLimit
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.registerOnPageChangeCallback(viewPagerPageCallback)
        (binding.viewPager.getChildAt(0) as RecyclerView).setItemViewCacheSize(pathList.size)
        binding.viewPager.reduceDragSensitivity()
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = listNameList[position]
        }.attach()

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

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(viewPagerPageCallback)
    }

    private val viewPagerPageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // pagerAdapter.getSelectedBaseFragment(position)?.initAdView()
            super.onPageSelected(position)
        }
    }

}