package com.oyetech.extension

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.os.SystemClock
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.databinding.ViewStubProxy
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.oyetech.baseOperation.R
import com.oyetech.core.utils.ResultEvent
import timber.log.Timber

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun Fragment.showSnackbar(
    snackbarText: String,
    timeLength: Int = BaseTransientBottomBar.LENGTH_LONG,
) {
    activity?.let {
        Snackbar.make(it.findViewById<View>(android.R.id.content), snackbarText, timeLength).show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun Fragment.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<ResultEvent<Int>>,
    timeLength: Int,
) {
    snackbarEvent.observe(
        lifecycleOwner,
        Observer { event ->
            event.getContentIfNotHandled()?.let { res ->
                context?.let { showSnackbar(it.getString(res), timeLength) }
            }
        }
    )
}

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

inline var View.isHidden: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) View.GONE else View.VISIBLE
    }

inline var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

fun View.setShown(isShown: Boolean) {
    if (isShown) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

fun View.setInvisibleView(isShown: Boolean) {
    if (isShown) {
        visibility = View.VISIBLE
    } else {
        visibility = View.INVISIBLE
    }
}

fun View?.removeViewFromLayout() {
    if (this == null) return
    (this.parent as? ViewGroup)?.removeAllViews()
}

fun View?.addViewToLayout(tmpView: View) {
    if (this == null) return
    tmpView.removeViewFromLayout()
    (this.parent as? ViewGroup)?.addView(tmpView)
}

fun View.switchVisibility() {
    if (visibility == View.VISIBLE) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun <T : ViewDataBinding> ViewStubProxy?.getViewStubBinding(): T {
    var binding = this?.viewStub?.inflate()
    binding?.show()

    return this?.binding as T
}

fun ViewStubProxy?.showViewStubProxy() {
    var binding = this?.viewStub?.inflate()
    binding?.show()
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.showWithAnim() {
    this.visibility = View.VISIBLE
    val animate = TranslateAnimation(1f, 1f, 1f, 0f) // toYDelta
    animate.duration = 3000
    animate.fillAfter = true
    this.startAnimation(animate)
}

fun View.showWithAnimSlide() {
    this.visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_down_anim)
    this.startAnimation(animation)
}

fun View.startDragView() {
    val data = ClipData.newPlainText("", "")
    val shadowBuilder = View.DragShadowBuilder(this)
    this.startDrag(data, shadowBuilder, this, 0)
}

fun View.triggerMotionEventAction(action: Int) {
    var motionEvent = generateMotionEvent(action)
    this.dispatchTouchEvent(motionEvent)
}

fun generateMotionEvent(actionInt: Int): MotionEvent? {
    val downTime: Long = SystemClock.uptimeMillis()
    val eventTime: Long = SystemClock.uptimeMillis() + 100
    val x = 0.0f
    val y = 0.0f
// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
    val metaState = 0
    val motionEvent = MotionEvent.obtain(
        downTime,
        eventTime,
        actionInt,
        x,
        y,
        metaState
    )

    return motionEvent
}

fun EditText.makeEdittextNewLineProperty() {
    this.inputType = InputType.TYPE_CLASS_TEXT or
            // InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or
            InputType.TYPE_TEXT_FLAG_MULTI_LINE
    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
}

fun View.hideKeyBoard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.makeToast(message: String?) {
    if (message == null) Timber.d("message null ")
    Timber.d("make toast === " + message)
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.makeToast(message: String?) {
    if (message == null) Timber.d("message null ")
    Timber.d("make toast === " + message)
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
}

fun Context.makeToast(message: String?) {
    if (message == null) Timber.d("message null ")
    Timber.d("make toast === " + message)
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
