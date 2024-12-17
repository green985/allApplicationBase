package com.oyetech.glidemodule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.oyetech.core.contextHelper.getApplicationLogo
import com.oyetech.core.ext.doInTryCatch
import com.oyetech.domain.repository.GlideOperationRepository
import timber.log.Timber

class GlideOperationRepositoryImp(private val context: Context) : GlideOperationRepository {

//    lateinit var context: Context

    override fun getBitmapWithUrl(url: String?, bitmapLoadAction: ((Bitmap?) -> Unit)) {
        if (url.isNullOrBlank()) {
            doInTryCatch {
                bitmapLoadAction.invoke(getAppIconUrl())
            }
        }

        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    doInTryCatch {
                        bitmapLoadAction.invoke(getAppIconUrl())
                    }
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?,
                ) {
                    bitmapLoadAction.invoke(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })


    }

    override fun setUseCaseContext(context: Context) {
        // this.context = context
    }

    private fun getAppIconUrl(): Bitmap? {

        val bm = context.getApplicationLogo()?.toBitmap()


        if (bm == null) {
            Timber.d("logoooo booşşş")
        } else {
            Timber.d("logoooo")
        }

        return bm
    }
    /*
        private fun getAppIconUrl(): BitmapDrawable? {
            // BitmapUtil.getBitmapFromDrawableResource(context,R.mipmap.ic_launcher)

            var bitmap = ResourcesCompat.getDrawable(
                context.getResources(),
                R.mipmap.ic_launcher_round,
                null
            ) as BitmapDrawable?

            return bitmap
        }

     */

}