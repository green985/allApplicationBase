package com.oyetech.imageviewer

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.oyetech.base.BaseActivity
import com.oyetech.core.stringOperation.StringHelper
import com.oyetech.cripto.activityArgs.ActivityBundleKey
import com.oyetech.extension.makeToast
import com.oyetech.extension.permissions.checkIsPermissionAlreadyGranted
import com.oyetech.helper.dialogs.showPermissionMustRequiredDialog
import com.oyetech.imageviewer.databinding.ActivityImageViewerBinding
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.materialViews.customViews.imageViewer.ImageViewerOverlayView
import com.oyetech.materialViews.helper.popupMenu.PopupMenuHelper
import com.oyetech.materialViews.old.helper.glideHelper.setImageUrlToView
import com.oyetech.materialViews.old.viewBindings.imageView.shareImageViewToShareIntent
import com.stfalcon.imageviewer.StfalconImageViewer
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.Calendar

class ImageViewerActivity :
    BaseActivity<ActivityImageViewerBinding, ImageViewerVm>(),
    ImageViewerOverlayView.ImageViewerOverlayViewRepository {

    override val layoutId: Int
        get() = R.layout.activity_image_viewer

    lateinit var weakImageView: WeakReference<ImageView>

    override val viewModel: ImageViewerVm by viewModel()
    private var viewer: StfalconImageViewer<String>? = null
    private var overlayView: ImageViewerOverlayView? = null

    private fun getImageUrlFromBundle(): ArrayList<String>? {
        var imageUrlList = intent.getStringArrayListExtra(ActivityBundleKey.IMAGE_URL_LIST)
        if (imageUrlList.isNullOrEmpty()) {
            makeToast(WallpaperLanguage.DEFAULT_ERROR)
            finish()
        }

        return imageUrlList!!
    }

    private fun getStartImagePosition() {
        var imagePosition = intent.getIntExtra(ActivityBundleKey.IMAGE_START_POSITION, 0)
        viewModel.imagePosition = imagePosition
    }

    override fun prepareView() {
        var imageUrlList = getImageUrlFromBundle()

        if (imageUrlList == null) {
            finish()
            return
        }

        if (imageUrlList.size == 0) {
            finish()
            return
        }
        viewModel.imageUrlList = imageUrlList
        getStartImagePosition()
        initOverlayViewProperty()
        initImageViewerProperty()
    }

    override fun prepareObserver() {
    }

    private fun initOverlayViewProperty() {
        overlayView = ImageViewerOverlayView(this)
        overlayView?.setLayoutRepository(this)
        overlayView?.setButtonProperties()
        overlayView?.setDotIndicatorDotSize(viewModel.getImageUrlList().size)
    }

    private fun initImageViewerProperty() {

        val builder = StfalconImageViewer.Builder<String>(
            this,
            viewModel.getImageUrlList()
        ) { imageView, imageUrl ->
            // viewModel.imageUrl = image
            showImage(imageView, imageUrl)
        }
            .withHiddenStatusBar(false)
            .withImageChangeListener {
                // will be fixed.
                overlayView?.setDotIndicatorSelectedDot(it)
            }
            .withDismissListener {
                finish()
            }
        builder.withStartPosition(viewModel.imagePosition)
        builder.withTransitionFrom(weakImageView.get())
        builder.withOverlayView(overlayView)
        builder.allowZooming(true)
        builder.allowSwipeToDismiss(true)

        overlayView?.setDotIndicatorSelectedDot(viewModel.imagePosition)

        viewer = builder.show()
    }

    private fun showImage(imageView: ImageView, imageUrl: String) {
        weakImageView = WeakReference(imageView)
        showRegularImage(imageView, imageUrl)
    }

    private fun showRegularImage(imageView: ImageView, imageUrl: String) {
        imageView.setImageUrlToView(imageUrl, true)
    }

    override fun setBackButtonAction() {
        finish()
    }

    override fun setOptionButtonAction(view: View) {
        // open share or report menu dialog.
        var popupMenuHelper = PopupMenuHelper(this, view)
        popupMenuHelper.openImageViewerOptionPopupMenu(::shareImageClick, ::downloadImageClick)
    }

    override fun moreLikeThisButtonHandler(view: View) {
        //TODO
    }

    private fun shareImageClick() {
        Timber.d("shareImageClick")
        weakImageView.get()?.shareImageViewToShareIntent()
    }

    private fun downloadImageClick() {
        Timber.d("downloadImageClick")
        var url = viewModel.getImageUrlList()[viewModel.imagePosition]
        if (url.isNullOrBlank()) {
            makeToast(WallpaperLanguage.DEFAULT_ERROR)
            return
        }
        controlPermissionForDownloadImage(url)
    }

    private fun downloadImage(uriString: String) {
        var appNameString = StringHelper.getApplicationName(this)

        val request = DownloadManager.Request(Uri.parse(uriString))
        request.setDescription(WallpaperLanguage.DOWNLOAD_IMAGE_DESC)
        request.setTitle(appNameString)

        // request.addRequestHeader(CriptoClassFile.IMAGE_CLIENT_KEY, HelperConstant.IMAGE_HEADER)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "$appNameString images/" + Calendar.getInstance().timeInMillis.toString() + ".png"
        )
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.allowScanningByMediaScanner()
        val downloadManager =
            this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        if (downloadManager == null) {
            Timber.d("downloadManager Null")
            return
        }
        downloadManager.enqueue(request)
    }

    fun controlPermissionForDownloadImage(uriString: String) {
        viewModel.downloadedImageUrl = uriString
        if (this.checkIsPermissionAlreadyGranted(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            downloadImage(uriString)
        } else {
            requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (permissions.isNullOrEmpty()) return
        if (grantResults.isEmpty()) return
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            //do nothing
            onPermissionDenied()
        }
    }

    fun requestPermission(permissions: Array<String>) {
        this?.let {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(permissions, 101)
            }
        }
    }

    open fun onPermissionDenied() {
        var fileFlag = false
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            fileFlag = true
        }
        if (fileFlag) {
            showPermissionMustRequiredDialog(WallpaperLanguage.WRITE_EXTERNAL_REQUIRED)
        }
    }

    fun onPermissionGranted() {
        Timber.d("onPermissionGranted")
        downloadImage(uriString = viewModel.downloadedImageUrl)
    }

}
