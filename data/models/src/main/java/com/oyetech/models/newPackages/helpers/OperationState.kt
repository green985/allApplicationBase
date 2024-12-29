package com.oyetech.models.newPackages.helpers

sealed class OperationState<out T> {
    object Idle : OperationState<Nothing>() // Henüz bir işlem başlatılmadı.
    object Loading : OperationState<Nothing>() // İşlem devam ediyor.
    data class Success<out T>(val data: T) : OperationState<T>() // İşlem başarılı tamamlandı.

    //    data class Error(val message: String, val cause: Throwable? = null) : OperationState<Nothing>() // İşlem başarısız oldu.
    data class Error(val exception: Exception) : OperationState<Nothing>() // İşlem başarısız oldu.
}