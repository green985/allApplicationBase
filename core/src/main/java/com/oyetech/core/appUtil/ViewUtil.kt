package com.oyetech.core.appUtil

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewStub
import android.view.ViewTreeObserver.OnWindowFocusChangeListener
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.GridLayout
import android.widget.GridLayout.LayoutParams
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.oyetech.core.ext.doInTryCatch
import com.oyetech.core.listenable.ListenableFuture
import com.oyetech.core.listenable.SettableFuture
import com.oyetech.core.serviceUtil.ServiceUtil.getInputMethodManager
import com.oyetech.core.viewUtils.Stub

object ViewUtil {
    fun focusAndMoveCursorToEndAndOpenKeyboard(input: EditText) {
        val numberLength = input.text.length
        input.setSelection(numberLength, numberLength)
        focusAndShowKeyboard(input)
    }

    fun focusAndShowKeyboard(view: View) {
        view.requestFocus()
        if (view.hasWindowFocus()) {
            showTheKeyboardNow(view)
        } else {
            view.viewTreeObserver.addOnWindowFocusChangeListener(object :
                OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    if (hasFocus) {
                        showTheKeyboardNow(view)
                        view.viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
        }
    }

    private fun showTheKeyboardNow(view: View) {
        if (view.isFocused) {
            view.post {
                val inputMethodManager = getInputMethodManager(view.context)
                inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    fun <T : View?> inflateStub(parent: View, @IdRes stubId: Int): T {
        return (parent.findViewById<View>(stubId) as ViewStub).inflate() as T
    }

    fun <T : View?> findStubById(parent: Activity, @IdRes resId: Int): Stub<T> {
        return Stub(parent.findViewById(resId))
    }

    fun <T : View?> findStubById(parent: View, @IdRes resId: Int): Stub<T> {
        return Stub(parent.findViewById(resId))
    }

    fun getSystemWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getSystemHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun calculateSystemAspectRatio(context: Context): Float {
        var systemWidth = ViewUtil.getSystemWidth(context).toFloat()
        var systemHeight = ViewUtil.getSystemHeight(context).toFloat()
        var aspectRatio = 0F
        if (systemWidth > systemHeight) {
            aspectRatio = (systemWidth / systemHeight)
        } else {
            aspectRatio = (systemHeight / systemWidth)
        }

        return aspectRatio
    }

    private fun getAlphaAnimation(from: Float, to: Float, duration: Int): Animation {
        val anim: Animation = AlphaAnimation(from, to)
        anim.interpolator = FastOutSlowInInterpolator()
        anim.duration = duration.toLong()
        return anim
    }

    fun fadeIn(view: View, duration: Int) {
        animateIn(view, getAlphaAnimation(0f, 1f, duration))
    }

    @JvmOverloads
    fun fadeOut(view: View, duration: Int, visibility: Int = View.GONE): ListenableFuture<Boolean> {
        return animateOut(view, getAlphaAnimation(1f, 0f, duration), visibility)
    }

    @JvmOverloads
    fun animateOut(
        view: View,
        animation: Animation,
        visibility: Int = View.GONE,
    ): ListenableFuture<Boolean> {
        val future: SettableFuture<Boolean> = SettableFuture<Boolean>()
        if (view.visibility == visibility) {
            future.set(true)
        } else {
            view.clearAnimation()
            animation.reset()
            animation.startTime = 0
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    view.visibility = visibility
                    future.set(true)
                }
            })
            view.startAnimation(animation)
        }
        return future
    }

    fun animateIn(view: View, animation: Animation) {
        if (view.visibility == View.VISIBLE) return
        view.clearAnimation()
        animation.reset()
        animation.startTime = 0
        view.visibility = View.VISIBLE
        view.startAnimation(animation)
    }

    fun <T : View?> inflate(
        inflater: LayoutInflater,
        parent: ViewGroup,
        @LayoutRes layoutResId: Int,
    ): T {
        return inflater.inflate(layoutResId, parent, false) as T
    }

    @SuppressLint("RtlHardcoded")
    fun setTextViewGravityStart(textView: TextView, context: Context) {
        if (isRtl(context)) {
            textView.gravity = Gravity.RIGHT
        } else {
            textView.gravity = Gravity.LEFT
        }
    }

    fun mirrorIfRtl(view: View, context: Context) {
        if (isRtl(context)) {
            view.scaleX = -1.0f
        }
    }

    fun isLtr(view: View): Boolean {
        return isLtr(view.context)
    }

    fun isLtr(context: Context): Boolean {
        return context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR
    }

    fun isRtl(view: View): Boolean {
        return isRtl(view.context)
    }

    fun isRtl(context: Context): Boolean {
        return context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    fun pxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density + 0.5).toInt()
    }

    @JvmStatic
    fun dpToPx(dp: Int): Int {
        return Math.round(dp * Resources.getSystem().displayMetrics.density)
    }

    fun dpToSp(dp: Int): Int {
        return (dpToPx(dp) / Resources.getSystem().displayMetrics.scaledDensity).toInt()
    }

    fun spToPx(sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().displayMetrics
        ).toInt()
    }

    fun updateLayoutParams(view: View, width: Int, height: Int) {
        view.layoutParams.width = width
        view.layoutParams.height = height
        view.requestLayout()
    }

    fun updateLayoutParamsIfNonNull(view: View?, width: Int, height: Int) {
        if (view != null) {
            updateLayoutParams(view, width, height)
        }
    }

    fun setVisibilityIfNonNull(view: View?, visibility: Int) {
        if (view != null) {
            view.visibility = visibility
        }
    }

    fun getLeftMargin(view: View): Int {
        return if (isLtr(view)) {
            (view.layoutParams as MarginLayoutParams).leftMargin
        } else (view.layoutParams as MarginLayoutParams).rightMargin
    }

    fun getRightMargin(view: View): Int {
        return if (isLtr(view)) {
            (view.layoutParams as MarginLayoutParams).rightMargin
        } else (view.layoutParams as MarginLayoutParams).leftMargin
    }

    fun getTopMargin(view: View): Int {
        return (view.layoutParams as MarginLayoutParams).topMargin
    }

    fun setLeftMargin(view: View, margin: Int) {
        if (isLtr(view)) {
            (view.layoutParams as MarginLayoutParams).leftMargin = margin
        } else {
            (view.layoutParams as MarginLayoutParams).rightMargin = margin
        }
        view.forceLayout()
        view.requestLayout()
    }

    fun setRightMargin(view: View, margin: Int) {
        if (isLtr(view)) {
            (view.layoutParams as MarginLayoutParams).rightMargin = margin
        } else {
            (view.layoutParams as MarginLayoutParams).leftMargin = margin
        }
        view.forceLayout()
        view.requestLayout()
    }

    fun setTopMargin(view: View, margin: Int) {
        (view.layoutParams as MarginLayoutParams).topMargin = margin
        view.requestLayout()
    }

    fun setBottomMargin(view: View, margin: Int) {
        (view.layoutParams as MarginLayoutParams).bottomMargin = margin
        view.requestLayout()
    }

    fun getWidth(view: View): Int {
        return view.layoutParams.width
    }

    fun setPaddingTop(view: View, padding: Int) {
        view.setPadding(view.paddingLeft, padding, view.paddingRight, view.paddingBottom)
    }

    fun setPaddingBottom(view: View, padding: Int) {
        view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, padding)
    }

    fun setPadding(view: View, padding: Int) {
        view.setPadding(padding, padding, padding, padding)
    }

    fun setPaddingStart(view: View, padding: Int) {
        if (isLtr(view)) {
            view.setPadding(padding, view.paddingTop, view.paddingRight, view.paddingBottom)
        } else {
            view.setPadding(view.paddingLeft, view.paddingTop, padding, view.paddingBottom)
        }
    }

    fun setPaddingEnd(view: View, padding: Int) {
        if (isLtr(view)) {
            view.setPadding(view.paddingLeft, view.paddingTop, padding, view.paddingBottom)
        } else {
            view.setPadding(padding, view.paddingTop, view.paddingRight, view.paddingBottom)
        }
    }

    fun isPointInsideView(view: View, x: Float, y: Float): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]
        return x > viewX && x < viewX + view.width && y > viewY && y < viewY + view.height
    }

    fun getStatusBarHeight(view: View): Int {
        var result = 0
        val resourceId = view.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = view.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getNavigationBarHeight(view: View): Int {
        var result = 0
        val resourceId = view.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = view.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun hideKeyboard(context: Context, view: View) {
        val inputManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Enables or disables a view and all child views recursively.
     */
    fun setEnabledRecursive(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            val viewGroup = view
            for (i in 0 until viewGroup.childCount) {
                setEnabledRecursive(viewGroup.getChildAt(i), enabled)
            }
        }
    }

    /**
     * Find view list by class name
     */
    fun <T : View> ViewGroup.getViewsByType(tClass: Class<T>): List<T> {
        return mutableListOf<T?>().apply {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                (child as? ViewGroup)?.let {
                    addAll(child.getViewsByType(tClass))
                }
                if (tClass.isInstance(child)) add(tClass.cast(child))
            }
        }.filterNotNull()
    }

    /**
     * Find view list by class name
     */
    fun <T : ViewBinding> ViewGroup.getViewBindingByType(tClass: Class<T>): List<T> {
        doInTryCatch {

            return mutableListOf<T?>().apply {
                for (i in 0 until childCount) {
                    val child = getChildAt(i)
                    (child as? ViewGroup)?.let {
                        addAll(child.getViewBindingByType(tClass))
                    }
                    if (tClass.isInstance(child)) add(tClass.cast(child))
                }
            }.filterNotNull()
        }

        return emptyList()
    }

    fun getActivityLifecycle(view: View): Lifecycle? {
        return getActivityLifecycle(view.context)
    }

    private fun getActivityLifecycle(context: Context?): Lifecycle? {
        return if (context is ContextThemeWrapper) {
            getActivityLifecycle(context.baseContext)
        } else null
        /*
    if (context instanceof AppCompatActivity) {
      return ((AppCompatActivity) context).getLifecycle();
    }


 */
    }

    fun getAllViews(rootView: View): List<View>? {
        val allViews: MutableList<View> = ArrayList()
        collectViews(rootView, allViews)
        return allViews
    }

    private fun collectViews(view: View, allViews: MutableList<View>) {
        if (view is ViewGroup) {
            val viewGroup = view
            val childCount = viewGroup.childCount
            for (i in 0 until childCount) {
                val childView = viewGroup.getChildAt(i)
                allViews.add(childView)
                // Recursively call collectViews to traverse through nested views
                collectViews(childView, allViews)
            }
        } else {
            // If the view is not a ViewGroup, it's a leaf node, add it to the list
            allViews.add(view)
        }
    }

    fun getScreenResolution(context: Context): Pair<Int, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        return Pair(screenWidth, screenHeight)
    }

    fun TextView.charLocation(offset: Int): Point? {
        layout ?: return null // Layout may be null right after change to the text view

        val lineOfText = layout.getLineForOffset(offset)
        val xCoordinate = layout.getPrimaryHorizontal(offset).toInt()
        val yCoordinate = layout.getLineTop(lineOfText)
        return Point(xCoordinate, yCoordinate)
    }

    fun getGridLayoutOneRowTwoItemLayoutParams(): LayoutParams {

        val layoutParams = LayoutParams()
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1F)

        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT

        return layoutParams
    }
}
