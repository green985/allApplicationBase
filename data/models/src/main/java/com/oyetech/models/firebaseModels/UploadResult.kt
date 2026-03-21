package com.oyetech.models.firebaseModels

sealed class UploadResult {
    data class Success(val downloadUrl: String) : UploadResult()
    data class Progress(val percentage: Int) : UploadResult()
    data class Error(val exception: Exception) : UploadResult()
}
