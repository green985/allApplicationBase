package com.oyetech.wallpaperList.ui.wallpaperList;

import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.oyetech.base.BaseFragment
import com.oyetech.cripto.privateKeys.WallpaperAppFragmentArgs
import com.oyetech.extension.permissions.isWritePermissionNotAskedClick
import com.oyetech.helper.dialogs.showPermissionMustRequiredDialog
import com.oyetech.helper.interfaces.RecyclerViewItemClickListener
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.materialViews.helper.viewHelpers.toolbarHelper.SimpleToolbarHelperLayout
import com.oyetech.models.utils.const.PermissionConstant
import com.oyetech.models.wallpaperModels.responses.WallpaperResponseData
import com.oyetech.wallpaperList.R
import com.oyetech.wallpaperList.databinding.FragmentSearchWallpaperBinding
import com.oyetech.wallpaperList.ui.richSearch.RichSearchFragment
import com.oyetech.wallpaperList.ui.wallpaperList.adapter.WallpaperListAdapter
import com.oyetech.wallpaperList.ui.wallpaperList.helper.setUpSearchView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-7.02.2024-
-19:25-
 **/

class WallpaperListFragment : BaseFragment<FragmentSearchWallpaperBinding, WallpaperListVM>(),
    RecyclerViewItemClickListener<WallpaperResponseData> {

    companion object {
        fun newInstance(sortingKey: String): WallpaperListFragment {
            val fragment = WallpaperListFragment()
            val bundles = bundleOf(Pair(WallpaperAppFragmentArgs.WALLPAPER_SORTING_KEY, sortingKey))
            fragment.arguments = bundles
            return fragment
        }
    }

    override val viewModel: WallpaperListVM by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_search_wallpaper

    lateinit var adapter: WallpaperListAdapter

    val fragmentArgs: WallpaperListFragmentArgs by navArgs()

    var sortingListKey = ""

    override fun prepareView() {
        viewModel.initWallpaperList(sortingListKey, queryString = fragmentArgs.queryString)
        prepareAdapter()
        prepareSetupAndSearchOperationViews()
    }

    override fun prepareToolbar() {
        var toolbarTitle = ""

        if (fragmentArgs.categoryName.isNullOrBlank()) {
            toolbarTitle = WallpaperLanguage.MORE_LIKE_THIS
        } else {
            toolbarTitle = fragmentArgs.categoryName ?: ""
        }


        SimpleToolbarHelperLayout.changeToolbarTitleCenter(
            binding.includeToolbar,
            title = toolbarTitle,
            center = false,
            isVisible = fragmentArgs.queryString?.isNotBlank() == true
        )
    }

    override fun prepareObserver() {
        viewModel.delayedLiveData.observe(viewLifecycleOwner) {

        }

        viewModel.downloadWallpaperPermissionRequestLiveData.observe(viewLifecycleOwner) {
            Timber.d("downloadWallpaperPermissionRequestLiveData === " + it)
            requestPermissionForDownloadWallpaper()
        }
    }

    override fun onPermissionDenied(requestCode: Int) {
        val permissionWriteNotAskAgainFlag = requireActivity().isWritePermissionNotAskedClick()
        Timber.d("permisson not asked agai nnnnnn == %s", permissionWriteNotAskAgainFlag)

        if (permissionWriteNotAskAgainFlag) {
            requireContext().showPermissionMustRequiredDialog(WallpaperLanguage.STORAGE_PERMISSION)
            Timber.d("permisson not asked agai nnnnnn")
        } else {
            Timber.d("fuckkkkkkk")
        }
    }

    private fun requestPermissionForDownloadWallpaper() {
        this.requestPermission(
            arrayOf(PermissionConstant.writeExternalPermission),
            PermissionConstant.writePermissionCode
        )
    }

    override fun getBundleArgs() {
        val sortingKey = arguments?.getString(WallpaperAppFragmentArgs.WALLPAPER_SORTING_KEY)
        if (sortingKey.isNullOrBlank()) {
            // do nothing
        } else {
            sortingListKey = sortingKey
        }

    }

    private fun prepareAdapter() {
        adapter = WallpaperListAdapter(this, retryAction = {
            viewModel.retry()
        })
        binding.rvWallpaperList.canErrorRetryClick = true
        binding.rvWallpaperList.retrySwipeAction = {
            invalidateList(isFromRefresh = true)
        }

        binding.rvWallpaperList.initRecyclerViewAdapter(adapter)

        viewModel.wallpaperPagedList.observe(viewLifecycleOwner) { page ->
            Timber.d("wallpaperPagedList  == " + page.size)
            viewModel.delayedLiveData.observe(viewLifecycleOwner) {
                adapter.submitList(page)
            }

        }

        viewModel.getInitalState().observe(viewLifecycleOwner, Observer {
            Timber.d("getInitalState  == " + it.toString())
            binding.rvWallpaperList.setInitalState(it)
        })
        viewModel.getNetworkState().observe(viewLifecycleOwner, Observer {
            adapter.setNetworkState(it)
            Timber.d("getNetworkState  == " + it.toString())
        })
    }

    fun invalidateList(isFromRefresh: Boolean = false) {
        val invalidateResult = viewModel.invalidatePaging(isFromRefresh)

        if (invalidateResult) {
            binding.rvWallpaperList.scrollToTop()
        }
    }

    fun prepareSetupAndSearchOperationViews() {
        setUpSearchView(
            getBaseActivity() as AppCompatActivity, binding.searchBar, binding.searchView
        )
    }

    fun attachRichSearchFragment() {
        val fragment = this.childFragmentManager.findFragmentByTag("searchFragment")
        if (fragment != null) {
            Timber.d("denemeeeeeeee fragment not nullllll")
            return
        }

        lifecycleScope.launch {
            this@WallpaperListFragment.childFragmentManager.beginTransaction().add(
                R.id.flRichSearchHolder,
                RichSearchFragment.newInstance(viewModel.wallpaperSearchOperationUseCase),
                "searchFragment"
            ).commit()
        }
    }

    override fun itemClickListener(
        adapterPosition: Int,
        itemData: WallpaperResponseData?,
        view: View?,
    ) {
        if (itemData == null) return
        if (view == null) return
        val id = view.id ?: return
        when (id) {
            R.id.ivWallpaperThumb -> {
                Timber.d("girdimmmm")
                viewModel.navigateToImageViewer(
                    context = requireActivity(),
                    transitionImageView = view as ImageView,
                    adapterPosition = adapterPosition
                )
                return
            }
        }
    }

}