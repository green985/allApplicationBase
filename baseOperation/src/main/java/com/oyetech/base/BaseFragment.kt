package com.oyetech.base

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.material.snackbar.Snackbar
import com.oyetech.baseOperation.R
import com.oyetech.core.ext.doInTryCatch
import com.oyetech.extension.hide
import com.oyetech.extension.makeToast
import com.oyetech.extension.permissions.checkPermissionCamera
import com.oyetech.extension.permissions.checkPermissionWriteData
import com.oyetech.extension.setupSnackbar
import com.oyetech.extension.show
import com.oyetech.helper.dialogs.showPermissionMustRequiredDialog
import com.oyetech.helper.language.LanguageHelper
import com.oyetech.helper.language.LanguageKeySet
import com.oyetech.models.utils.states.ViewState
import com.oyetech.models.utils.states.ViewStatus
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**
Created by Erdi Özbek
-8.02.2022-
-21:50-
 **/

abstract class BaseFragment<DB : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    lateinit var binding: DB
    abstract val viewModel: VM

    @get:LayoutRes
    abstract val layoutId: Int

    abstract fun prepareView()
    abstract fun prepareObserver()
    open fun initAdView() {}
    open fun prepareToolbar() {}
    open fun getBundleArgs() {}
    open fun setLayoutsInterfaces() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?,
    ): View? {
        Timber.d("onCreateView = ".plus(this.javaClass.toString()))

        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBundleArgs()
        //observeNavigation(viewModel, getBaseActivity()?.getAdHelperr())
        observeNavigation(viewModel)
        setupSnackbar(this, viewModel.snackBarError, Snackbar.LENGTH_LONG)
        initAdView()
        prepareToolbar()
        prepareView()
        setLayoutsInterfaces()
        prepareObserver()
        initBindingVariable()
        setFragmentResultListeners()
        setFragmentStaticViewClickAndText()
    }

    private fun observeNavigation(viewModel: BaseViewModel) {
//        doInTryCatch {
//            viewModel.navigation.observe(
//                viewLifecycleOwner,
//                Observer {
//                    it?.getContentIfNotHandled()?.let { command ->
//                        when (command) {
//                            is To -> {
//                                findNavController().navigate(
//                                    resId = command.directions.actionId,
//                                    args = command.directions.arguments,
//                                    navOptions = getNavigationAnimOptions(),
//                                    navigatorExtras = getExtras()
//
//                                )
//                            }
//
//                            is Back -> findNavController().navigateUp()
//                        }
//                    }
//                }
//            )
//        }
    }

    fun setBindingVariableAndExecute(
        variableId: Int,
        model: Any,
        bindingTmp: ViewDataBinding? = null,
    ) {
        if (bindingTmp != null) {
            bindingTmp.setVariable(variableId, model)
            bindingTmp.executePendingBindings()
            return
        }
        binding.setVariable(variableId, model)
        binding.executePendingBindings()
    }

    /*
        private fun observeNavigation(viewModel: BaseViewModel, adHelperr: ApplicationAdHelper?) {
            doInTryCatch {
                viewModel.navigation.observe(
                    viewLifecycleOwner,
                    Observer {
                        it?.getContentIfNotHandled()?.let { command ->
                            when (command) {
                                is NavigationCommand.To -> {
                                    findNavController().navigate(
                                        resId = command.directions.actionId,
                                        args = command.directions.arguments,
                                        navOptions = getNavigationAnimOptions(),
                                        navigatorExtras = getExtras()

                                    )
                                    viewModel.controlAndShowAds(adHelperr, command)
                                }

                                is NavigationCommand.Back -> findNavController().navigateUp()
                            }
                        }
                    }
                )
            }
        }

    fun showInterstitialAds(): Boolean {
        return getBaseActivity()?.getAdHelperr()?.showAd() ?: false
    }

     */

    open fun setFragmentResultListeners() {
    }

    open fun setFragmentStaticViewClickAndText() {
    }

    open fun initBindingVariable() {
        Timber.d("Binding Variable Initialize")
        binding.executePendingBindings()
    }

    fun setBottomBarVisibility(isVisible: Boolean) {
        getBaseActivityViewModel()?.setBottomBarNavigationVisibility(isVisible)
    }

    fun getBaseActivityViewModel(): BaseViewModel? {
        doInTryCatch {

            var activity = requireActivity() as BaseActivity<*, *>
            return activity.viewModel
        }
        return null
    }

    fun getBaseActivity(): BaseActivity<*, *>? {
        doInTryCatch {

            var activity = requireActivity() as BaseActivity<*, *>
            return activity
        }
        return null
    }

    private fun getTClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (permissions.isNullOrEmpty()) return
        if (grantResults.isEmpty()) return
        Timber.d("grandResults == " + grantResults.toString())
        var controlFlag = true
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                controlFlag = false
            }
        }

        if (controlFlag) {
            onPermissionGranted(requestCode)
        } else {
            onPermissionDenied(requestCode)
        }
    }

    open fun onPermissionGranted(requestCode: Int) = Unit

    open fun onPermissionDenied(requestCode: Int) {
        var cameraFlag = false
        var fileFlag = false

        if (!requireContext().checkPermissionCamera()) {
            cameraFlag = true
        }

        if (!requireContext().checkPermissionWriteData()) {
            fileFlag = true
        }

        if (cameraFlag && fileFlag) {
            requireContext().showPermissionMustRequiredDialog(LanguageKeySet.CAMERA_STORAGE_PERMISSION)
            // showPermissionMustRequiredDialog(R.string.permissionDenyDesc)
        } else {
            if (cameraFlag) {
                requireContext().showPermissionMustRequiredDialog(LanguageKeySet.CAMERA_PERMISSION)
            } else {
                requireContext().showPermissionMustRequiredDialog(LanguageKeySet.STORAGE_PERMISSION)
            }
        }
    }

    fun requestPermission(permissions: Array<String>, requestCode: Int) {
        activity?.let {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(permissions, requestCode)
            }
        }
    }

    fun initBackButtonPress(action: (() -> Unit)) {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    action.invoke()
                }
            }
        )
    }

    fun ViewDataBinding.observeLoadingState(viewState: ViewState<*>?) {
        if (viewState?.isLoading == true) {
            this.root.visibility = View.VISIBLE
        } else {
            this.root.visibility = View.GONE
        }
    }

    open fun getExtras(): FragmentNavigator.Extras = FragmentNavigatorExtras()

    fun getNavigationAnimOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    }

    private fun setLoadingVisibility(setShow: Boolean) {
        (activity as BaseActivity<*, *>).setLoadingVisibility(setShow)
    }

    fun getErrorTranslateStringFromViewState(errorr: ViewState<Boolean>): String {
        var errorLanguageKey = errorr.message ?: ""
        var translationErrorString = LanguageHelper.getStringWithKeyNull(errorLanguageKey) ?: ""
        return translationErrorString ?: LanguageKeySet.DEFAULT_ERROR
    }

    fun observeLoadingState(
        viewState: ViewState<*>,
        disableToast: Boolean = false,
        hideRootView: View? = null,
    ) {
        if (viewState.status == ViewStatus.LOADING) {
            setLoadingVisibility(true)
            hideRootView?.hide()
        } else {
            setLoadingVisibility(false)
            hideRootView?.show()
        }

        if (!disableToast) {
            if (viewState.status == ViewStatus.ERROR) {
                if (viewState.message != null) {
                    var errorLanguageKey = viewState.message ?: ""
                    var translationErrorString =
                        LanguageHelper.getStringWithKeyNull(errorLanguageKey)
                    if (translationErrorString != null) {
                        makeToast(translationErrorString)
                    } else {
                        makeToast(viewState.message)
                    }
                } else {
                    makeToast(LanguageKeySet.DEFAULT_ERROR)
                }
            }
        }
    }

    /*
        fun getBannerAdView(bannerAdType: BannerAdViewType): View? {
            return getBaseActivity()?.adHelper?.getBannerAdView(bannerAdType)
        }

     */
}
