package com.oyetech.base

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Base Holder Class for [RecyclerView.ViewHolder]
 */
abstract class BaseHolder<M, DB : ViewDataBinding> constructor(
    private val viewDataBinding: DB,
) : RecyclerView.ViewHolder(viewDataBinding.root) {

    var adViewFlag = true
    var adViewAction: (() -> Unit)? = null

    /**
     * Getter for [M] class
     */
    var item: M? = null

    /**
     * Variable id that goes to layout
     */
    abstract fun bindingVariable(): Int

    /**
     * Set data to layout
     * @param data -> Model object
     */
    fun doBindings(data: M?) {
        if (bindingVariable() == 0) {
            this.item = data
            return
        }
        viewDataBinding.setVariable(bindingVariable(), data)
        viewDataBinding.executePendingBindings()
        this.item = data
    }

    /**
     * Binds holder data
     */
    abstract fun bind()

    /**
     * Getter for Row Class item [M]
     */
    fun getRowItem(): M? {
        return item
    }

    fun getRowBinding(): DB {
        return viewDataBinding
    }

    fun getRoot(): View {
        return viewDataBinding.root
    }

    /*
    fun prepareAdLayoutWithoutRemove(
        adRootView: View,
        adViewFirst: AdManagerAdView?,
        adViewSecond: AdManagerAdView?,
        criteoAdHelper: CriteoAdHelper? = null
    ) {
        if (criteoAdHelper == null) return

        if (adViewFirst == null || adViewSecond == null) {
            adRootView.hide()
            return
        }

        var isFirstAdAddedPosition =
            calculateAdAddedPosition(criteoAdHelper, adViewFirst, true)

        var isSecondAdAddedPosition =
            calculateAdAddedPosition(criteoAdHelper, adViewSecond, false)

        if (isFirstAdAddedPosition) {
            Timber.d("position === " + absoluteAdapterPosition)
        }
        if (isSecondAdAddedPosition) {
            Timber.d("position2 === " + absoluteAdapterPosition)
        }
        if (isFirstAdAddedPosition && isSecondAdAddedPosition) {
            Timber.d("position3 === ikisi aynı === " + absoluteAdapterPosition)
        }

        if (isFirstAdAddedPosition || isSecondAdAddedPosition) {
            val childCount = (adRootView as ViewGroup).childCount
            Timber.d("childCoutn reklamlı" + childCount)
            if (childCount == 0) {
                prepareAdLayout(adRootView, adViewFirst, adViewSecond, criteoAdHelper)
            }
        }
    }

    fun prepareAdLayout(
        adRootView: View,
        adViewFirst: AdManagerAdView?,
        adViewSecond: AdManagerAdView?,
        criteoAdHelper: CriteoAdHelper? = null
    ) {
        if (criteoAdHelper == null) return
        if (adViewAction == null) {
            adViewAction = {
                prepareAdLayoutWithoutRemove(adRootView, adViewFirst, adViewSecond, criteoAdHelper)
            }
        }
        if (adViewFirst == null || adViewSecond == null) {
            adRootView.hide()
            return
        }
        var isFirstAdAddedPosition =
            calculateAdAddedPosition(criteoAdHelper, adViewFirst, true)

        var isSecondAdAddedPosition =
            calculateAdAddedPosition(criteoAdHelper, adViewSecond, false)

        if (isFirstAdAddedPosition) {
            Timber.d("position === " + absoluteAdapterPosition)
        }
        if (isSecondAdAddedPosition) {
            Timber.d("position2 === " + absoluteAdapterPosition)
        }
        if (isFirstAdAddedPosition && isSecondAdAddedPosition) {
            Timber.d("position3 === ikisi aynı === " + absoluteAdapterPosition)
        }

        if (isFirstAdAddedPosition || isSecondAdAddedPosition) {
            if (isFirstAdAddedPosition) {
                (adRootView as ViewGroup).removeAllViews()
                adViewFirst.removeViewFromLayout()
                (adRootView as ViewGroup).addView(adViewFirst)
                Timber.d(".adUnitId == " + adViewFirst.adUnitId)
                addMarginToAdView(adViewFirst)
                if (criteoAdHelper.preload) {
                    criteoAdHelper?.refreshAdForLoadAddType(adViewSecond)
                } else {
                    criteoAdHelper?.refreshAdForLoadAddType(adViewFirst)
                }
                adRootView.show()
                return
            }
            (adRootView as ViewGroup).removeAllViews()
            adViewSecond.removeViewFromLayout()
            Timber.d(".adUnitId == " + adViewSecond.adUnitId)
            (adRootView as ViewGroup).addView(adViewSecond)
            addMarginToAdView(adViewSecond)
            if (criteoAdHelper.preload) {
                criteoAdHelper?.refreshAdForLoadAddType(adViewFirst)
            } else {
                criteoAdHelper?.refreshAdForLoadAddType(adViewSecond)
            }
            adRootView.show()
        } else {
            adRootView.hide()
            (adRootView as ViewGroup).removeAllViews()
        }
    }

    fun addMarginToAdView(publisherAdView: AdManagerAdView) {
        if (publisherAdView.responseInfo == null) {
            Timber.d("margin eklenemedi response info null")
            return
        }
        var paddingDp =
            publisherAdView.resources.getDimension(R.dimen.topic_list_vertical_margin).toInt()
        var heightDp =
            publisherAdView.resources.getDimension(R.dimen.ad_view_height_dp).toInt()
        var params = publisherAdView.layoutParams as? ViewGroup.MarginLayoutParams
        if (params == null) {
            Timber.d("margin eklenemedi params info null")
            return
        }
        params.height = heightDp
        params.setMargins(0, paddingDp, 0, paddingDp)
        publisherAdView.requestLayout()
    }

    fun calculateAdAddedPosition(
        criteoAdHelper: CriteoAdHelper? = null,
        publisherAdView: AdManagerAdView,
        isFirst: Boolean
    ): Boolean {
        if (criteoAdHelper == null) return false
        var value = false
        var adUnitId = publisherAdView.adUnitId
        var adDisplayType = criteoAdHelper.adDisplayType
        var isEntryAd = false
        if (adUnitId.contains("entry")) {
            isEntryAd = true
        }
        if (adDisplayType != CriteoConst.MinimizeAdViews) {
            value = CriteoConst.calculateAdForList(absoluteAdapterPosition, isFirst, isEntryAd)
            if (value) {
                Timber.d("adapertttt == " + absoluteAdapterPosition + " == unit  == " + adUnitId)
            }
            return value
        } else {
            value = CriteoConst.calculateAdForListMinimizeType(
                absoluteAdapterPosition,
                isFirst,
                isEntryAd
            )
            return value
        }

        return false

    }

     */
}
