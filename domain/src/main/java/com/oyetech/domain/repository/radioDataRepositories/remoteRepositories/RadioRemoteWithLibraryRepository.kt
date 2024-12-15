package com.oyetech.domain.repository.radioDataRepositories.remoteRepositories

import com.oyetech.models.radioProject.radioModels.RadioDataModel
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-22.11.2022-
-17:26-
 **/

interface RadioRemoteWithLibraryRepository {

    suspend fun getRadioList(): Flow<List<RadioDataModel>>
}