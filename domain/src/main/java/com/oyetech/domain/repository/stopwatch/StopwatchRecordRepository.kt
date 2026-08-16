package com.oyetech.domain.repository.stopwatch

import kotlinx.coroutines.flow.Flow

interface StopwatchRecordRepository {
    suspend fun insertRecord(
        startedAt: Long,
        endedAt: Long,
        durationSeconds: Int,
        status: StopwatchRecordStatus,
        tag: StopwatchTag? = null,
    )

    fun getAll(): Flow<List<StopwatchRecord>>
}

enum class StopwatchRecordStatus { FINISHED, CANCELLED }

enum class StopwatchTag(val label: String) {
    KAHVALTI("Kahvaltı"),
    OGLE_YEMEGI("Öğle Yemeği"),
    AKSAM_YEMEGI("Akşam Yemeği"),
    ARA_OGUN("Ara Öğün"),
    KUCUK_GOREV("Küçük Görev"),
    ORTA_GOREV("Orta Görev"),
    BUYUK_GOREV("Büyük Görev"),

    SIGARA("Sigara"),
    YUMRUK("Yumruk"),
    KAHVE("Kahve"),
    BIRA("Bira"),
    SU_IC("Su İç"),
    ODAK("Odak"),

    MEDITASYON("Meditasyon"),
    NEFES_EGZERSIZI("Nefes Egzersizi"),
    ESNEME("Esneme"),
    SCHROTH("Schroth"),
    POSTUR("Postür"),

    YUZME("Yüzme"),
    YURUYUS("Yürüyüş"),
    BARFIKS("Barfiks"),

    YEMEK_HAZIRLAMA("Yemek Hazırlama"),
    YEMEK_YEME("Yemek Yeme"),

    KITAP_OKUMA("Kitap Okuma"),
    YAZMA("Yazma"),
    DUSUNME("Düşünme"),

    YAZILIM("Yazılım"),
    KODLAMA("Kodlama"),
    HATA_AYIKLAMA("Hata Ayıklama"),
    PLANLAMA("Planlama"),

    TEMIZLIK("Temizlik"),
    CAFE("Cafe"),
    DINLENME("Dinlenme"),

    DENEME("Deneme"),
}

/**
 * Model passed when starting a stopwatch session.
 * Duration is expressed in seconds so both second- and minute-based selections are supported.
 * Future fields such as note or task can be added here without changing the start signature.
 */
data class StopwatchSession(
    val durationSeconds: Int,
    val tag: StopwatchTag? = null,
)

data class StopwatchRecord(
    val id: Long,
    val startedAt: Long,
    val endedAt: Long,
    val durationSeconds: Int,
    val status: StopwatchRecordStatus,
    val tag: StopwatchTag? = null,
)

