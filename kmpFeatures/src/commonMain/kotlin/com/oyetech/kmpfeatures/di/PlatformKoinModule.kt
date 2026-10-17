package com.oyetech.kmpfeatures.di

import org.koin.core.module.Module

expect val platformKoinModule: Module

internal expect suspend fun playTimerFinishedSound()

internal expect val diaryHtmlDownloader: ((fileName: String, html: String) -> Unit)?
