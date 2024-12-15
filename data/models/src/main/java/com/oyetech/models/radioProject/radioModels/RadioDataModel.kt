package com.oyetech.models.radioProject.radioModels

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class RadioDataModel(
    @PrimaryKey(autoGenerate = true) var rowId: Long = 0,
    var radioStreamUrl: String = "",
    var radioName: String = "",
    var radioPlayingSongName: String = "",
    var countryCode: String = "",
    var tags: String = "",
    var clickcount: Int = 0,
    var bitrate: Int = 0,
    var clicktrend: Int = 0,
    @Transient
    @Ignore
    var languageList: MutableList<String> = mutableListOf(),
    @Transient
    @Ignore var lastcheckoktime: Date = Date(),
    var radioUrl: String = "",
    var stationUUID: String = "",
    var votes: Int = 0,

    var radioTitle: String = ""
) : Parcelable, Any() {

}

object RadioDataModelMapper {
    fun mapFromStation(mapper: (() -> RadioDataModel)): RadioDataModel {
        return mapper.invoke()
    }
}

