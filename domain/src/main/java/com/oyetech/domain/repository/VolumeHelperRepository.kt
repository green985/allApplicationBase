package com.oyetech.domain.repository

interface VolumeHelperRepository {

    fun volumeUp()
    fun volumeDown()
    fun volumeMaxWithDelay(delay: Long)
}