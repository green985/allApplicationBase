package com.oyetech.kmpfeatures.auth

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
actual fun googleWebClientId(): String = js("window.KMP_GOOGLE_CLIENT_ID || ''")
