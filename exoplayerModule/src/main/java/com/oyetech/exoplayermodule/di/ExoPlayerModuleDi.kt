package com.oyetech.exoplayermodule.di

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.oyetech.domain.repository.contentOperation.ExoPlayerOperationRepository
import com.oyetech.exoplayermodule.analytics.ExoplayerAnalyticsListener
import com.oyetech.exoplayermodule.helper.ExoPlayerRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-15.11.2022-
-14:38-
 **/

private val appName = "Radioooo"

object ExoPlayerModuleDi {

    val exoPlayerModule = module {
        single { provideDefaultBandwidthMeter(get()) }
        single { provideAdaptiveTrackSelectionFactory() }
        single { provideDefaultTrackSelector(get()) }
        single { provideDefaultDataSource(get()) }
        single { provideDefaultDataSourceFactory(get()) }
        single { provideDefaultExtractorsFactory() }
        single { provideDefaultHlsDataSourceFactory(get()) }
        single { provideHlsMediaSourceFactory(get()) }
        single { provideProgressiveMediaSourceFactory() }
        single { provideSimpleExoPlayer(get()) }
        single { ExoplayerAnalyticsListener(get()) }
        // singleOf(::ExoPlayerRepositoryImp)

        single<ExoPlayerOperationRepository> { ExoPlayerRepositoryImp(get(), get()) }

    }
}

fun provideDefaultBandwidthMeter(context: Context): DefaultBandwidthMeter {
    return DefaultBandwidthMeter.Builder(context).build()
}

fun provideAdaptiveTrackSelectionFactory(): AdaptiveTrackSelection.Factory {
    return AdaptiveTrackSelection.Factory()
}

fun provideDefaultTrackSelector(context: Context): DefaultTrackSelector {
    return DefaultTrackSelector(context)
}

fun provideDefaultDataSource(context: Context): DefaultDataSource {
    return DefaultDataSource(context, Util.getUserAgent(context, appName), true)
}

fun provideDefaultDataSourceFactory(
    context: Context,
): DefaultDataSource.Factory {
    return DefaultDataSource.Factory(
        context
    )
}

fun provideDefaultExtractorsFactory(): DefaultExtractorsFactory {
    return DefaultExtractorsFactory()
}

fun provideDefaultHlsDataSourceFactory(defaultExtractorsFactory: DefaultDataSource.Factory): DefaultHlsDataSourceFactory {
    return DefaultHlsDataSourceFactory(defaultExtractorsFactory)
}

fun provideHlsMediaSourceFactory(defaultExtractorsFactory: DefaultHlsDataSourceFactory): HlsMediaSource.Factory {
    return HlsMediaSource.Factory(defaultExtractorsFactory)
}

fun provideProgressiveMediaSourceFactory(): ProgressiveMediaSource.Factory {
    return ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
}

fun provideSimpleExoPlayer(
    context: Context,
): ExoPlayer {
    var factoryy = DefaultMediaSourceFactory(context)
    return ExoPlayer.Builder(context, factoryy).build()
}

/*

    @Provides
    @Singleton
    internal fun provideExoPlayer(): String {
        return "I Provide exo Player Module"
    }

    @Provides
    @Singleton
    internal fun provideDefaultBandwidthMeter(): DefaultBandwidthMeter {
        return DefaultBandwidthMeter()
    }

    @Provides
    @Singleton
    internal fun provideAdaptiveTrackSelectionFactory(): AdaptiveTrackSelection.Factory {
        return AdaptiveTrackSelection.Factory()
    }

    @Provides
    @Singleton
    internal fun provideDefaultTrackSelector(): DefaultTrackSelector {
        return DefaultTrackSelector()
    }


    @Provides
    @Singleton
    internal fun provideDefaultDataSource(context: Context, userAgent: String): DefaultDataSource {
        return DefaultDataSource(context, Util.getUserAgent(context, javaClass.simpleName), true)
    }

    @Provides
    @Singleton
    internal fun provideDefaultDataSourceFactory(
        context: Context
    ): DefaultDataSourceFactory {
        return DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, javaClass.simpleName),
            null
        )
    }


    @Provides
    @Singleton
    internal fun provideDefaultExtractorsFactory(): DefaultExtractorsFactory {
        return DefaultExtractorsFactory()
    }

    @Provides
    @Singleton
    internal fun provideDefaultHlsDataSourceFactory(defaultExtractorsFactory: DefaultDataSourceFactory): DefaultHlsDataSourceFactory {
        return DefaultHlsDataSourceFactory(defaultExtractorsFactory)
    }


    //MediaSource
    @Provides
    @Singleton
    internal fun provideExtractorMediaSourceFactory(
        dataSource: DefaultDataSourceFactory,
        defaultExtractorsFactory: DefaultExtractorsFactory
    ): ExtractorMediaSource.Factory {
        return ExtractorMediaSource.Factory(dataSource).setExtractorsFactory(
            defaultExtractorsFactory
        )
    }

    //MediaSource
    @Provides
    @Singleton
    internal fun provideHlsMediaSourceFactory(defaultExtractorsFactory: DefaultHlsDataSourceFactory): HlsMediaSource.Factory {
        return HlsMediaSource.Factory(defaultExtractorsFactory)
    }

    @Provides
    @Singleton
    internal fun provideSimpleExoPlayer(
        context: Context,
        trackSelector: DefaultTrackSelector
    ): SimpleExoPlayer {
        return ExoPlayerFactory.newSimpleInstance(context, trackSelector)
    }

 */