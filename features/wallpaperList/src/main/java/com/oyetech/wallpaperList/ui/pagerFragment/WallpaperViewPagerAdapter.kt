package com.oyetech.wallpaperList.ui.pagerFragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.oyetech.base.BaseFragment
import com.oyetech.wallpaperList.ui.wallpaperList.WallpaperListFragment

/**
Created by Erdi Özbek
-23.02.2024-
-15:48-
 **/

class WallpaperViewPagerAdapter(
    fragment: Fragment,
    private var sortingKeyList: List<String>,
) :
    FragmentStateAdapter(fragment) {

    private val fragmentList = HashMap<Int, BaseFragment<*, *>>()

    override fun getItemCount(): Int = sortingKeyList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment

        fragment = WallpaperListFragment.newInstance(sortingKey = sortingKeyList[position])
        fragmentList.put(position, fragment)

        return fragment

    }

    fun getSelectedBaseFragment(position: Int): BaseFragment<*, *>? {
        return fragmentList.getOrDefault(position, null)
    }

}