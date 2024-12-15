package com.oyetech.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.oyetech.helper.sharedPref.SharedHelper
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**
Created by Erdi Özbek
-17.02.2022-
-23:58-
 **/

abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {
    lateinit var binding: DB

    companion object {

        init {
            Timber.d("askdnaısndkamsd")
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    // var adHelper: ApplicationAdHelper? = null
    abstract val viewModel: VM

    lateinit var viewModelFactory: ViewModelProvider.Factory

    @get:LayoutRes
    abstract val layoutId: Int

    lateinit var sharedHelper: SharedHelper

    var isBottomNavigationVisible = true

    private val loadingDialog: Dialog? by lazy {
        var dialog: Dialog? = null
        try {
            val dialogClass = Class.forName("com.oyetech.materialViews.old.dialogs.CustomDialog")
            // use my_class object to
            dialog = dialogClass.getConstructor(Context::class.java)
                .newInstance(this as Context) as Dialog
        } catch (ex: Exception) {
            // handle other exception
            ex.printStackTrace()
        }
        dialog
    }

    // private lateinit var errorDialog: ErrorDialogView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        binding = DataBindingUtil.setContentView(this, layoutId)

        prepareView()
        prepareObserver()
    }

    abstract fun prepareView()
    abstract fun prepareObserver()

    open fun showSnackBarWithMessage(message: String?, isError: Boolean = false) {}

    private fun getTClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ondestroy calleed = " + this.localClassName)
    }

    fun setLoadingVisibility(setShow: Boolean) {
        loadingDialog?.let {
            if (setShow) {
                if (!it.isShowing) {
                    it.show()
                }
            } else {
                if (it.isShowing) {
                    it.dismiss()
                }
            }
        }
    }
    /*

        fun checkShowGooglePlayDialogMissing(isFromLogin: Boolean = false) {
            doInTryCatch {
                var googlePlayCheck = this.checkGooglePlayServices()
                if (!googlePlayCheck) {
                    if (sharedHelper.getBooleanData(SharedPrefCons.GOOGLE_PLAY_MISSING_SHOW, false)) {
                        return@doInTryCatch
                    }
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.warningDialogText).toLowerCase(Locale("tr")))
                    builder.setMessage(getString(R.string.googlePlayMissing))
                    builder.setNegativeButton(getString(R.string.close)) { dialog, _ -> dialog.dismiss() }
                    builder.show()
                    sharedHelper.putBooleanData(SharedPrefCons.GOOGLE_PLAY_MISSING_SHOW, true)
                } else {
                    (viewModel as? MainVM)?.initNotificationRegisterInActivity(isFromLogin)
                }
            }
        }


        fun setBottomNavigationShowHide(isShown: Boolean) {
            if (!isBottomNavigationVisible) return
            try {
                if (isShown) {
                    bottomNavigation.show()
                } else {
                    bottomNavigation.hide()
                }
            } catch (e: Exception) {

            }
        }

        fun setBottomNavigationVisibility(isVisible: Boolean) {
            isBottomNavigationVisible = isVisible
            try {
                if (isVisible) {
                    bottomNavigation.show()
                } else {
                    bottomNavigation.hide()
                }
            } catch (e: Exception) {

            }
        }

        fun <T> observeLoadingState(viewState: ViewState<T>) {
            if (viewState.status == ViewStatus.LOADING) {
                setLoadingVisibility(true)
            } else {
                setLoadingVisibility(false)
            }
        }

        fun observeLoadingStateWithStatus(viewState: ViewStatus) {
            if (viewState == ViewStatus.LOADING) {
                setLoadingVisibility(true)
            } else {
                setLoadingVisibility(false)
            }
        }
     */
    /*
        fun getAdHelperr(): ApplicationAdHelper? {
            return adHelper
        }

     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            for (childFragments in fragment.childFragmentManager.fragments) {
                childFragments.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
