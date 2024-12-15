package com.oyetech.imageviewer

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.ImageView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.oyetech.core.stringOperation.StringHelper
import com.oyetech.domain.repository.viewHelperRepositories.ImageViewerOverlayRepository
import com.oyetech.domain.repository.viewHelperRepositories.ImageViewerRepository
import com.oyetech.extension.makeToast
import com.oyetech.extension.permissions.checkIsPermissionAlreadyGranted
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.materialViews.customViews.imageViewer.WallpaperImageOverlayView
import com.oyetech.materialViews.customViews.imageViewer.WallpaperImageOverlayView.WallpaperImageViewerOverlayViewRepository
import com.oyetech.materialViews.helper.popupMenu.PopupMenuHelper
import com.oyetech.materialViews.old.helper.glideHelper.GlideImageLoader
import com.oyetech.materialViews.old.helper.glideHelper.clear
import com.oyetech.materialViews.old.helper.glideHelper.setImageUrlToViewWithGlideImageLoader
import com.oyetech.materialViews.old.helper.glideHelper.setImageUrlToViewWithThumbnail
import com.oyetech.materialViews.old.viewBindings.imageView.shareImageViewToShareIntent
import com.oyetech.models.wallpaperModels.helperModels.image.ImageViewerPropertyData
import com.stfalcon.imageviewer.StfalconImageViewer
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.Calendar

/**
Created by Erdi Özbek
-22.02.2024-
-16:46-
 **/

class ImageViewerRepositoryImp : WallpaperImageViewerOverlayViewRepository,
    ImageViewerRepository {

    private var viewer: StfalconImageViewer<String>? = null
    private var overlayView: WallpaperImageOverlayView? = null
    private var glideImageLoader: GlideImageLoader? = null
    lateinit var weakImageView: WeakReference<ImageView>

    var imagePosition = 0

    var imageUrl = ""

    var downloadedImageUrl = ""
    lateinit var imagePropertyList: ArrayList<ImageViewerPropertyData>

    var imageViewerOverlayRepository: ImageViewerOverlayRepository? = null

    fun getImageUrlList(): List<String> {
        return imagePropertyList.map {
            it.imageUrl
        }
    }

    lateinit var context: Activity

    override fun initImageViewerWithTransition(
        activity: Activity,
        imageListProperty: ArrayList<ImageViewerPropertyData>,
        adapterPosition: Int,
        transitionImageView: ImageView?,
        overlayRepository: ImageViewerOverlayRepository,
    ) {
        context = activity
        imagePropertyList = imageListProperty
        imagePosition = adapterPosition
        imageViewerOverlayRepository = overlayRepository
        initOverlayViewProperty()
        initImageViewerProperty(transitionImageView = transitionImageView)

    }

    override fun onCleared() {
        viewer = null
        overlayView = null
        glideImageLoader = null
        weakImageView.clear()
    }

    private fun initOverlayViewProperty() {
        overlayView = WallpaperImageOverlayView(context)
        overlayView?.setLayoutRepository(this)
        overlayView?.setButtonProperties()
        overlayView?.setDotIndicatorDotSize(getImageUrlList().size)
    }

    private fun initImageViewerProperty(transitionImageView: ImageView?) {

        val builder = StfalconImageViewer.Builder<String>(
            context,
            getImageUrlList()
        ) { imageView, imageUrl ->
            // viewModel.imageUrl = image
            showImage(imageView, imageUrl)
        }
            .withHiddenStatusBar(false)
            .withImageChangeListener {
                glideImageLoader?.detachListener()
            }
            .withDismissListener {
                // finish()
                onCleared()
            }
        builder.withStartPosition(imagePosition)
        if (transitionImageView != null) {
            builder.withTransitionFrom(transitionImageView)
        }
        builder.withOverlayView(overlayView)
        builder.allowZooming(true)
        builder.allowSwipeToDismiss(true)


        overlayView?.setDotIndicatorSelectedDot(imagePosition)

        viewer = builder.show()
    }

    private fun showImage(imageView: ImageView, imageUrl: String) {
        this.imageUrl = imageUrl

        var thumbnailUrl = try {
            imagePropertyList.find {
                it.imageUrl == imageUrl
            }?.thumbnailUrl ?: ""
        } catch (e: Exception) {
            ""
        }

        imageView.clear()
        imageView.adjustViewBounds = true
        imageView.invalidate()
        weakImageView = WeakReference(imageView)

        showRegularImage(imageView, imageUrl, thumbnailUrl)
    }

    private fun showRegularImage(imageView: ImageView, imageUrl: String, thumbnailUrl: String) {
        // imageView.setImageUrlToView(imageUrl, true)
        if (thumbnailUrl == "") {
            imageView.setImageUrlToViewWithThumbnail(imageUrl, true, thumbnailPercent = 0.3f)
        } else {

            val imageProgressView = getProgressViewFromOverlayView()
            glideImageLoader = GlideImageLoader(imageView, imageProgressView)

            imageView.setImageUrlToViewWithGlideImageLoader(
                imageUrl, thumbnailUrl,
                glideImageLoader!!
            )


        }


    }

    private fun getProgressViewFromOverlayView(): LinearProgressIndicator? {
        var imageProgressView = overlayView?.getImageProgressBarView()
        imageProgressView?.progress = 0
        imageProgressView?.show()
        return imageProgressView
    }

    override fun setBackButtonAction() {
        // finish()
        viewer?.close()
    }

    override fun setOptionButtonAction(view: View) {
        // open share or report menu dialog.
        var popupMenuHelper = PopupMenuHelper(context, view)
        popupMenuHelper.openImageViewerOptionPopupMenu(::shareImageClick, ::downloadImageClick)
    }

    override fun moreLikeThisButtonHandler(view: View) {
        var currentPosition = viewer?.currentPosition() ?: -1
        if (currentPosition != -1) {
            var item = imagePropertyList.get(currentPosition)
            imageViewerOverlayRepository?.moreLikeThisButtonHandler(item)
        }
        viewer?.dismiss()
    }

    override fun downloadButtonHandler(view: View) {
        downloadImageClick()
    }

    private fun shareImageClick() {
        Timber.d("shareImageClick")
        weakImageView.get()?.shareImageViewToShareIntent()
    }

    private fun downloadImageClick() {
        Timber.d("downloadImageClick")
        var url = getImageUrlList()[imagePosition]
        if (url.isNullOrBlank()) {
            context.makeToast(WallpaperLanguage.DEFAULT_ERROR)
            return
        }
        controlPermissionForDownloadImage(url)
    }

    private fun downloadImage(uriString: String) {
        var appNameString = StringHelper.getApplicationName(context)

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
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        if (downloadManager == null) {
            Timber.d("downloadManager Null")
            return
        }
        downloadManager.enqueue(request)
        context.makeToast(WallpaperLanguage.DOWNLOAD_IMAGE_START)

    }

    fun controlPermissionForDownloadImage(uriString: String) {
        downloadedImageUrl = uriString

        if (Build.VERSION.SDK_INT >= 33) {
            downloadImage(uriString)
            return
        }


        if (context.checkIsPermissionAlreadyGranted(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            downloadImage(uriString)
        } else {
            imageViewerOverlayRepository?.requestDownloadPermission()
            // requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }


}