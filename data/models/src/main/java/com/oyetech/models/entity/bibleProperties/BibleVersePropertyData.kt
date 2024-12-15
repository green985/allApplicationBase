package com.oyetech.models.entity.bibleProperties

import android.os.Parcelable
import android.text.SpannedString
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Ignore
import com.oyetech.models.BR
import com.oyetech.models.utils.states.TextFontEnum
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class BibleVersePropertyData(
    @Json(name = "content")
    var content: String? = "",
    @Json(name = "verseId")
    var verseId: String? = "",
    @Json(name = "verseNotes")
    var verseNotes: List<BibleVerseNoteResponseData> = listOf(),
) : Parcelable

@Keep
data class BibleVerseWithTextDetailData(
    var content: String? = "",
    var spannedContent: SpannedString = SpannedString(""),
    var verseId: String? = "",
    var verseNotes: List<BibleVerseNoteResponseData> = listOf(),
    var tmpBibleVersePropertyData: BibleVersePropertyData,
) : BaseObservable() {

    @Ignore
    @IgnoredOnParcel
    @Transient
    var _contentFontSizeMultiplier: Float = 1.0f

    @Ignore
    @IgnoredOnParcel
    @Transient
    var _playingVerseAudio: Boolean = false

    @Ignore
    @IgnoredOnParcel
    @Transient
    var _contentFontStyle: TextFontEnum = TextFontEnum.DEFAULT

    var contentFontSizeMultiplier: Float
        @Bindable get() = _contentFontSizeMultiplier
        set(value) {
            _contentFontSizeMultiplier = value
            notifyPropertyChanged(BR.contentFontSizeMultiplier)
        }

    var contentFontStyle: TextFontEnum
        @Bindable get() = _contentFontStyle
        set(value) {
            _contentFontStyle = value
            notifyPropertyChanged(BR.contentFontStyle)
        }

    var playingVerseAudio: Boolean
        @Bindable get() = _playingVerseAudio
        set(value) {
            _playingVerseAudio = value
            notifyPropertyChanged(BR.playingVerseAudio)
        }
}