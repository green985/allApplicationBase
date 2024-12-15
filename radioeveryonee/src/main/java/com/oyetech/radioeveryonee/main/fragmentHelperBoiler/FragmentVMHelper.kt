package com.oyetech.holybible.fragmentHelperBoiler

/**
Created by Erdi Özbek
-30.06.2022-
-00:57-
 **/

/**
 * Fragment and view model boilerplate
 *
 */

fun main() {
}

fun containsNearbyDuplicate(nums: IntArray, k: Int): Boolean {
    nums.forEachIndexed { index1, i ->
        nums.forEachIndexed { index2, ll ->
            if (i == ll) {
                var resultt = (index1 - index2)
                if (resultt < 0) {
                    resultt = resultt * -1
                }
                var flag = resultt <= k
                if (flag) {
                    return true
                }
            }
        }
    }

    return false
}
/*

class FilterFragment : BaseFragment<*, *>() {

    companion object {
        fun newInstance() = FilterFragment()
    }

    override val viewModel: FilterVM by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_filter

    override fun prepareView() {
    }

    override fun prepareObserver() {
    }
}





class FilterVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {

    init {

    }
}

 */
