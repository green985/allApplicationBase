package com.oyetech.core.appUpdate

/**
Created by Erdi Özbek
-20/04/2024-
-11:05-
 **/

/** this file will hold app update function and operations
 * maybe not used in this project

fun Context.showUpdateAppDialog(isOptional: Boolean = false, afterAction: (() -> Unit)? = null) {
val builder = AlertDialog.Builder(this)
if (isOptional) {
builder.setMessage(LanguageKeySet.UPDATE_VERSION_OPTIONAL_INFO_TEXT)
} else {
builder.setMessage(LanguageKeySet.UPDATE_VERSION_FORCE_INFO_TEXT)
builder.setCancelable(false)
}

builder.setTitle(LanguageKeySet.VERSION_UPDATE_DIALOG_HEADER)
builder.setPositiveButton(LanguageKeySet.UPDATE) { dialog, _ ->
this@showUpdateAppDialog.openStoreUrl()
this.finishApp()
}
if (isOptional) {
builder.setNegativeButton(LanguageKeySet.CANCEL) { dialog, _ ->
afterAction?.invoke()
dialog.dismiss()
}
}
builder.show()
}



fun MainActivity.observeForceUpdateLiveData(forceUpdateStateEnum: ForceUpdateStateEnum) {
Timber.d("force update ==== " + forceUpdateStateEnum)
when (forceUpdateStateEnum) {
ForceUpdateStateEnum.FORCE_UPDATE -> {
showUpdateAppDialog(isOptional = false, afterAction = {})
}

ForceUpdateStateEnum.OPTIONAL_UPDATE -> {
showUpdateAppDialog(isOptional = true, afterAction = {})
}

ForceUpdateStateEnum.VERSION_DIALOG_NOT_NEED -> {
// do nothing...
}
}
}

 */