package com.oyetech.models.utils.states

data class ViewState<out T>(
    val status: ViewStatus,
    val data: T? = null,
    val message: String? = null,
) {
    companion object {
        fun <T> success(data: T? = null, message: String? = null): ViewState<T> {
            return ViewState(ViewStatus.SUCCESS, data, message)
        }

        fun <T> error(msg: String): ViewState<T> {
            return ViewState(ViewStatus.ERROR, null, msg)
        }

        fun <T> loading(data: T? = null): ViewState<T> {
            return ViewState(ViewStatus.LOADING, data, null)
        }

        fun <T> emptyList(message: String? = null): ViewState<T> {
            return ViewState(ViewStatus.NO_LIST, null, message)
        }
    }

    var isSuccess: Boolean = this.status == ViewStatus.SUCCESS
    var isLoading: Boolean = this.status == ViewStatus.LOADING
    var isError: Boolean = this.status == ViewStatus.ERROR
}

enum class ViewStatus {
    SUCCESS, LOADING, ERROR, NO_LIST
}
