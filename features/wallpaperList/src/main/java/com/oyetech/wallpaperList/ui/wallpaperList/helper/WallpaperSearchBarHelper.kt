package com.oyetech.wallpaperList.ui.wallpaperList.helper

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchView.TransitionState
import com.google.android.material.search.SearchView.TransitionState.SHOWN
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.wallpaperList.ui.wallpaperList.WallpaperListFragment
import timber.log.Timber

/**
Created by Erdi Özbek
-15.02.2024-
-13:33-
 **/

fun WallpaperListFragment.setUpSearchView(
    activity: AppCompatActivity,
    searchBar: SearchBar,
    searchView: SearchView,
) {
    var appBarLayout = binding.appBarLayout

    searchView.hint = WallpaperLanguage.SEARCH
    searchBar.hint = WallpaperLanguage.SEARCH

    if (sortingListKey == "random") {
        appBarLayout.setExpanded(true, true)
    }

    // searchView.inflateMenu(menu.cat_searchview_menu)
    searchView.setOnMenuItemClickListener { menuItem: MenuItem? ->
        // TODO
        true
    }

    if (!fragmentArgs.queryString.isNullOrBlank()) {
        searchBar.setText(fragmentArgs.queryString)
    }


    searchView
        .editText
        .setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            submitSearchQuery(
                searchBar,
                searchView,
                searchView.text.toString()
            )
            false
        }

    searchView.editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            viewModel.setSearchQuery(p0.toString())
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    })

    val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback( /* enabled= */false) {
            override fun handleOnBackPressed() {
                searchView.hide()
            }
        }
    activity.onBackPressedDispatcher.addCallback(activity, onBackPressedCallback)
    searchView.addTransitionListener { searchView1: SearchView?, previousState: TransitionState?, newState: TransitionState ->
        onBackPressedCallback.isEnabled = newState == SHOWN

        if (TransitionState.SHOWING == newState) {
            Timber.d("showinnnnngggg")
            attachRichSearchFragment()
        }
        if (TransitionState.SHOWN == newState) {
            Timber.d("shownnnnnnnnn")
        }

        if (TransitionState.HIDDEN == newState) {
            invalidateList()
        }
    }
}

fun submitSearchQuery(
    searchBar: SearchBar,
    searchView: SearchView,
    query: String,
) {
    searchBar.setText(query)
    searchView.hide()
}