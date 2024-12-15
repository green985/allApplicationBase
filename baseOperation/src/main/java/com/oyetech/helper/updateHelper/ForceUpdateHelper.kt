package com.oyetech.helper.updateHelper

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.oyetech.core.buildHelper.getVersionCode
import com.oyetech.cripto.stringKeys.SharedPrefKey
import com.oyetech.helper.sharedPref.SharedPrefRepositoryImp
import com.oyetech.models.entity.settings.AndroidVersionResponse
import com.oyetech.models.utils.helper.TimeFunctions
import java.util.Calendar
import java.util.Date

/**
Created by Erdi Özbek
-16.07.2022-
-15:52-
 **/

class ForceUpdateHelper(
    private var context: Context,
    private var sharedHelperRepository: SharedPrefRepositoryImp,
) {

    private var sharedHelper = sharedHelperRepository.getSharedHelperBase()
    var versionInfoLiveData = MutableLiveData<ForceUpdateStateEnum>()

    fun controlVersionTypeForce(androidVersionResponse: AndroidVersionResponse?): Boolean {
        if (androidVersionResponse == null) {
            versionInfoLiveData.value = ForceUpdateStateEnum.VERSION_DIALOG_NOT_NEED
            return false
        }
        var version = context.getVersionCode().toInt()
        if (androidVersionResponse.minBuildVersionNumber > version) {
            versionInfoLiveData.value = ForceUpdateStateEnum.FORCE_UPDATE
            return true
        } else if (androidVersionResponse.optionalWarningMinBuildVersionNumber > version) {
            if (controlOptionalUpdateTime()) {
                versionInfoLiveData.value = ForceUpdateStateEnum.OPTIONAL_UPDATE
            } else {
                versionInfoLiveData.value = ForceUpdateStateEnum.VERSION_DIALOG_NOT_NEED
            }
            return false
        }

        versionInfoLiveData.value = ForceUpdateStateEnum.VERSION_DIALOG_NOT_NEED
        return false
    }

    private fun controlOptionalUpdateTime(): Boolean {
        var milis = Calendar.getInstance().timeInMillis
        var optionalUpdateDate = sharedHelper.getLongData(SharedPrefKey.OPTIONAL_UPDATE_DATE, 0)
        if (optionalUpdateDate == 0L) {
            // first show
            sharedHelper.putLongData(
                SharedPrefKey.OPTIONAL_UPDATE_DATE,
                milis
            )
            return true
        } else {
            // Control time has come
            var expireDate =
                Date(optionalUpdateDate + TimeFunctions.calculateDayMilis(3))
            var currentDate = Date(milis)
            if (currentDate.after(expireDate)) {
                return true
            }
        }
        return false
    }
}

enum class ForceUpdateStateEnum {
    FORCE_UPDATE, OPTIONAL_UPDATE, VERSION_DIALOG_NOT_NEED
}
