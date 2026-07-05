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

enum class StopwatchTag {
    KAHVALTI,
    OGLE_YEMEGI,
    AKSAM_YEMEGI,
    ARA_OGUN,

    SIGARA,
    KAHVE,
    BIRA,
    SU_IC,

    MEDITASYON,
    NEFES_EGZERSIZI,
    ESNEME,
    SCHROTH,
    POSTUR,

    YUZME,
    YURUYUS,
    BARFIKS,
    DIP,
    SINAV,
    CORE,
    MOBILITE,

    YEMEK_HAZIRLAMA,
    YEMEK_YEME,

    KITAP_OKUMA,
    STOACILIK,
    GUNLUK,
    DUSUNME,

    YAZILIM,
    KODLAMA,
    HATA_AYIKLAMA,
    PLANLAMA,

    TEMIZLIK,
    CAFE,
    DINLENME,

    DENEME,
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

