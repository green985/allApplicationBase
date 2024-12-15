package com.oyetech.domain.repository.viewHelperRepositories

interface SmallPlayerCallback {
    fun onToggle()
}

/**
 * Fragment may be a part of another view which could be dragged/scrolled
 * and certain hacks may require the fragment to request them to stop
 * intercepting touch events to not end up confused.
 */
interface TouchInterceptListener {
    fun requestDisallowInterceptTouchEvent(disallow: Boolean)
}