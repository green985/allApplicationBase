package com.oyetech.helper.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.oyetech.helper.language.LanguageKeySet

/**
Created by Erdi Özbek
-6.04.2022-
-14:15-
 **/
/*
fun Context.showPermissionMustRequiredDialog(desc: Int) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("permissions")
    builder.setMessage(getString(desc))
    builder.setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
    builder.setPositiveButton("go setting") { dialog, _ ->
        dialog.dismiss()
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + this.packageName)
            )
        )
    }
    builder.show()
}


fun Context.showMicrophonePermissionMustRequiredDialog() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(LanguageKeySet.MICROPHONE_PERMISSION)
    builder.setMessage(LanguageKeySet.MICROPHONE_PERMISSION_INFO)
    builder.setNegativeButton(LanguageKeySet.CANCEL) { dialog, _ -> dialog.dismiss() }
    builder.setPositiveButton(LanguageKeySet.SETTINGS) { dialog, _ ->
        dialog.dismiss()
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + this.packageName)
            )
        )
    }
    builder.show()
}


 */


fun Context.showPermissionMustRequiredDialog(headerTitle: String) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(headerTitle)
    var message = ""

    when (headerTitle) {
        LanguageKeySet.CAMERA_PERMISSION -> {
            message = LanguageKeySet.CAMERA_PERMISSION_INFO
        }

        LanguageKeySet.STORAGE_PERMISSION -> {
            message = LanguageKeySet.STORAGE_PERMISSION_INFO
        }

        LanguageKeySet.CAMERA_STORAGE_PERMISSION -> {
            message = LanguageKeySet.CAMERA_STORAGE_PERMISSION_INFO
        }
    }
    builder.setMessage(message)

    builder.setNegativeButton(LanguageKeySet.CANCEL) { dialog, _ -> dialog.dismiss() }
    builder.setPositiveButton(LanguageKeySet.SETTINGS) { dialog, _ ->
        dialog.dismiss()
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + this.packageName)
            )
        )
    }
    builder.show()
}