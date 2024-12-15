package com.oyetech.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oyetech.core.ext.doInTryCatch
import com.oyetech.core.utils.SingleLiveEvent

/**
 * Manages the various graphs needed for a [BottomNavigationView].
 *
 * This sample is a workaround until the Navigation Component supports multiple back stacks.
 */
fun BottomNavigationView.setupWithNavController(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent,
    reselectLiveData: SingleLiveEvent<Int>? = null,
    animList: List<Int>,
): LiveData<NavController> {

    // Map of tags
    val graphIdToTagMap = SparseArray<String>()
    // Result. Mutable live data with the selected controlled
    val selectedNavController = MutableLiveData<NavController>()

    var firstFragmentGraphId = 0

    // First create a NavHostFragment for each NavGraph ID
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)

        // Find or create the Navigation host fragment
        val navHostFragment =
            obtainNavHostFragment(fragmentManager, fragmentTag, navGraphId, containerId)

        // Obtain its _id
        val graphId = navHostFragment.navController.graph.id

        if (index == 0) {
            firstFragmentGraphId = graphId
        }

        // Save to the map
        graphIdToTagMap.put(graphId, fragmentTag)
        // Timber.d("fragmentTag==" + fragmentTag)
        // Attach or detach nav host fragment depending on whether it's the selected item.
        if (this.selectedItemId == graphId) {
            // Update livedata with the selected graph
            selectedNavController.value = navHostFragment.navController
            attachNavHostFragment(fragmentManager, navHostFragment, index == 0)
        } else {
            detachNavHostFragment(fragmentManager, navHostFragment)
        }
    }

    // Now connect selecting an item with swapping Fragments
    var selectedItemTag = graphIdToTagMap.get(this.selectedItemId)
    val firstFragmentTag = graphIdToTagMap.get((firstFragmentGraphId))
    var isOnFirstFragment = selectedItemTag == firstFragmentTag

    // When a navigation item is selected
    setOnNavigationItemSelectedListener { item ->
        // Don't do anything if the state is state has already been saved.
        if (fragmentManager.isStateSaved) {
            false
        } else {
            val newlySelectedItemTag = graphIdToTagMap.get(item.itemId)
            if (selectedItemTag != newlySelectedItemTag) {
                // Pop everything above the first fragment (the "fixed start destination")
                fragmentManager.popBackStack(
                    firstFragmentTag,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                val selectedFragment =
                    fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment

                // Exclude the first fragment tag because it's always in the back stack.
                if (firstFragmentTag != newlySelectedItemTag) {
                    // Commit a transaction that cleans the back stack and adds the first fragment
                    // to it, creating the fixed started destination.
                    fragmentManager.beginTransaction()
                        .attach(selectedFragment)
                        .setPrimaryNavigationFragment(selectedFragment)
                        .apply {
                            // Detach all other Fragments
                            graphIdToTagMap.forEach { _, fragmentTagIter ->
                                if (fragmentTagIter != newlySelectedItemTag) {
                                    detach(fragmentManager.findFragmentByTag(firstFragmentTag)!!)
                                }
                            }
                        }
                        .addToBackStack(firstFragmentTag)
                        /*
                    .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )

                         */
                        .setCustomAnimations(animList[0], animList[1], animList[2], animList[3])
                        .setReorderingAllowed(true)
                        .commit()
                }
                selectedItemTag = newlySelectedItemTag
                isOnFirstFragment = selectedItemTag == firstFragmentTag
                selectedNavController.value = selectedFragment.navController
                true
            } else {
                false
            }
        }
    }

    // Optional: on item reselected, pop back stack to the destination of the graph
    setupItemReselected(graphIdToTagMap, fragmentManager, reselectLiveData)

    // Handle deep link
    setupDeepLinks(navGraphIds, fragmentManager, containerId, intent)

    // Finally, ensure that we update our BottomNavigationView when the back stack changes
    fragmentManager.addOnBackStackChangedListener {
        if (!isOnFirstFragment && !fragmentManager.isOnBackStack(firstFragmentTag)) {
            this.selectedItemId = firstFragmentGraphId
        }

        // Reset the graph if the currentDestination is not valid (happens when the back
        // stack is popped after using the back button).
        selectedNavController.value?.let { controller ->
            if (controller.currentDestination == null) {
                controller.navigate(controller.graph.id)
            }
        }
    }
    return selectedNavController
}

fun BottomNavigationView.navigateDeeplink(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    uri: Uri,
) {
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)

        // Find or create the Navigation host fragment
        val navHostFragment =
            obtainNavHostFragment(fragmentManager, fragmentTag, navGraphId, containerId)
        // Handle deeplink
        val canHandleDeeplink = navHostFragment.navController.graph.hasDeepLink(uri)

        if (canHandleDeeplink) {
            if (selectedItemId != navHostFragment.navController.graph.id) {
                selectedItemId = navHostFragment.navController.graph.id
            }
        }
    }
}

private fun BottomNavigationView.setupDeepLinks(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent,
) {
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)

        // Find or create the Navigation host fragment
        val navHostFragment =
            obtainNavHostFragment(fragmentManager, fragmentTag, navGraphId, containerId)
        // Handle Intent
        if (navHostFragment.navController.handleDeepLink(intent) && selectedItemId != navHostFragment.navController.graph.id) {
            this.selectedItemId = navHostFragment.navController.graph.id
        }
    }
}

@SuppressLint("RestrictedApi")
private fun BottomNavigationView.setupItemReselected(
    graphIdToTagMap: SparseArray<String>,
    fragmentManager: FragmentManager,
    reselectLiveData: SingleLiveEvent<Int>? = null,
) {
    setOnNavigationItemReselectedListener { item ->
        com.oyetech.core.ext.doInTryCatch {
            val newlySelectedItemTag = graphIdToTagMap.get(item.itemId)
            val selectedFragment =
                fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment
            val navController = selectedFragment.navController

            if (reselectLiveData?.hasActiveObservers() == true) {
                var index = getBottomNavigationIndex(newlySelectedItemTag)
                reselectLiveData?.value = index
            }
            /*
            if (navController.backQueue.size == 2) {
                // reselectLiveData.removeObservers(selectedFragment.viewLifecycleOwner)
                if (reselectLiveData?.hasActiveObservers() == true) {
                    var index = getBottomNavigationIndex(newlySelectedItemTag)
                    reselectLiveData?.value = index
                }
            }

             */
            // Pop the back stack to the start destination of the current navController graph
            navController.popBackStack(navController.graph.startDestinationId, false)
        }
    }
}

/*
@SuppressLint("RestrictedApi")
fun FragmentManager.removeBackStackAndNavigate(
    tabIndex: Int, action: () -> Unit
) {

    val newlySelectedItemTag = getFragmentTag(tabIndex)

    val selectedFragment = obtainNavHostFragment(
        this,
        newlySelectedItemTag,
        NavigationExt.navGraphIds[tabIndex],
        R.id.nav_host_container
    ) as NavHostFragment

    selectedFragment.lifecycleScope.launchWhenResumed {
        // Wait for fragment to restore state from backStack
        // otherwise navigate will be ignored
        // Ignoring navigate() call: FragmentManager has already saved its state
        val navController = selectedFragment.navController
        var pop = navController.popBackStack(
            navController.graph.startDestinationId, false
        )
        Timber.d("graph == message " + pop)
        action.invoke()
    }
    /*

     Timber.d("attach func  before")

     Timber.d("attach func  before")
     Timber.d("isState Fuck == " + (selectedFragment as Fragment).childFragmentManager.isStateSaved)

    // Pop the back stack to the start destination of the current navController graph
    if (navController.graph.startDestination == R.id.messages_dest) {
         Timber.d("graph == message dopğru")
    } else {
         Timber.d("graph == message yanlış")

    }
    var pop = navController.popBackStack(
        navController.graph.startDestination, false
    )
     Timber.d("graph == message " + pop)

     */
}

 */

private fun detachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment,
) {
    fragmentManager.beginTransaction().detach(navHostFragment).commitNow()
}

private fun attachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment,
    isPrimaryNavFragment: Boolean,
) {
    fragmentManager.beginTransaction().attach(navHostFragment).apply {
        if (isPrimaryNavFragment) {
            setPrimaryNavigationFragment(navHostFragment)
        }
    }.commitNow()
}

fun obtainNavHostFragment(
    fragmentManager: FragmentManager,
    fragmentTag: String,
    navGraphId: Int,
    containerId: Int,
): NavHostFragment {
    // If the Nav Host fragment exists, return it
    val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
    existingFragment?.let { return it }

    // Otherwise, create it and return it.
    val navHostFragment = NavHostFragment.create(navGraphId)
    fragmentManager.beginTransaction().add(containerId, navHostFragment, fragmentTag).commitNow()
    return navHostFragment
}

private fun FragmentManager.isOnBackStack(backStackName: String): Boolean {
    val backStackCount = backStackEntryCount
    for (index in 0 until backStackCount) {
        if (getBackStackEntryAt(index).name == backStackName) {
            return true
        }
    }
    return false
}

fun getFragmentTag(index: Int) = "bottomNavigation#$index"
fun getBottomNavigationIndex(fragmentTag: String): Int {
    var index = fragmentTag.removePrefix("bottomNavigation#")
    doInTryCatch {

        return index.toInt()
    }
    return -1
}
