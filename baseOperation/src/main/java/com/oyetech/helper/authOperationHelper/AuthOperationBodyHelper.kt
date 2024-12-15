package com.oyetech.helper.authOperationHelper

import android.content.Context
import com.oyetech.core.buildHelper.getOSVersion
import com.oyetech.core.buildHelper.getPlatform
import com.oyetech.core.buildHelper.getVersionCode
import com.oyetech.core.buildHelper.getVersionName
import com.oyetech.core.deviceExt.DeviceHelperExt
import com.oyetech.core.deviceExt.DeviceHelperExt.getDeviceName
import com.oyetech.cripto.privateKeys.PrivateKeys
import com.oyetech.cripto.stringKeys.SharedPrefKey
import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.helper.sharedPref.SharedHelper
import com.oyetech.models.postBody.auth.AuthRequestBody
import com.oyetech.models.postBody.auth.BibleSaveDeviceRequestBody
import com.oyetech.models.postBody.auth.FacebookSignUpRequestBody
import com.oyetech.models.postBody.auth.GoogleSignUpRequestBody
import com.oyetech.models.postBody.auth.NonSocialSignUpRequestBody
import com.oyetech.models.postBody.auth.RefreshAuthRequestBody
import com.oyetech.models.postBody.auth.RegisterRequestBody
import com.oyetech.models.postBody.notification.NotificationTokenRequestBody
import com.oyetech.models.postBody.settings.SettingsInfoRequestBody

/**
Created by Erdi Özbek
-2.03.2022-
-18:16-
 **/

class AuthOperationBodyHelper(
    private var sharedHelper: SharedHelper,
    private var sharedOperationUseCase: SharedOperationUseCase,
    private var context: Context,
) {

    // just set username and password and send request
    fun generateAuthOperation(isFromNewLogin: Boolean = false): AuthRequestBody {
        if (isFromNewLogin) {
            removeClientSecretForNewLogin()
        }

        context.apply {
            var authRequestBody =
                AuthRequestBody(
                    apiSecret = getApiKey(),
                    buildNumber = getVersionCode(),
                    clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                    clientUniqueId = sharedOperationUseCase.getClientUniqSecretOrProduce(),
                    deviceModel = getDeviceName(),
                    osVersion = getOSVersion(),
                    password = "",
                    platform = getPlatform(),
                    usernameOrEmail = "",
                    version = getVersionName()
                )
            return authRequestBody
        }
    }

    fun generateGoogleAuthRequestBody(googleToken: String): GoogleSignUpRequestBody {
        context.apply {

            var googleSignUpRequestBody =
                GoogleSignUpRequestBody(
                    apiSecret = getApiKey(),
                    buildNumber = getVersionCode(),
                    clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                    clientUniqueId = sharedOperationUseCase.getClientUniqSecretOrProduce(),
                    deviceModel = getDeviceName(),
                    osVersion = getOSVersion(),
                    platform = getPlatform(),
                    version = getVersionName(),
                    idToken = googleToken
                )
            return googleSignUpRequestBody
        }
    }

    fun generateNonSocialAuthRequestBody(token: String): NonSocialSignUpRequestBody {
        context.apply {

            var nonSocialSignUpRequestBody =
                NonSocialSignUpRequestBody(
                    apiSecret = getApiKey(),
                    buildNumber = getVersionCode(),
                    clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                    clientUniqueId = sharedOperationUseCase.getClientUniqSecretOrProduce(),
                    deviceModel = getDeviceName(),
                    osVersion = getOSVersion(),
                    platform = getPlatform(),
                    version = getVersionName(),
                    idToken = token
                )
            return nonSocialSignUpRequestBody
        }
    }

    fun generateFacebookAuthRequestBody(facebookToken: String): FacebookSignUpRequestBody {
        context.apply {

            var facebookSignUpRequestBody =
                FacebookSignUpRequestBody(
                    apiSecret = getApiKey(),
                    buildNumber = getVersionCode(),
                    clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                    clientUniqueId = sharedOperationUseCase.getClientUniqSecretOrProduce(),
                    deviceModel = getDeviceName(),
                    osVersion = getOSVersion(),
                    platform = getPlatform(),
                    version = getVersionName(),
                    accessToken = facebookToken
                )
            return facebookSignUpRequestBody
        }
    }

    fun generateRegisterCompleteAuthRequestBody(): RegisterRequestBody {
        context.apply {

            var registerRequestBody =
                RegisterRequestBody(
                    apiSecret = getApiKey(),
                    buildNumber = getVersionCode(),
                    clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                    clientUniqueId = sharedOperationUseCase.getClientUniqSecretOrProduce(),
                    deviceModel = getDeviceName(),
                    osVersion = getOSVersion(),
                    platform = getPlatform(),
                    version = getVersionName()
                )
            return registerRequestBody
        }
    }

    fun generateRefreshAuthOperation(): RefreshAuthRequestBody {
        context.apply {

            var refreshAuthRequestBody =
                RefreshAuthRequestBody(
                    apiSecret = getApiKey(),
                    buildNumber = getVersionCode(),
                    clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                    clientUniqueId = sharedOperationUseCase.getClientUniqSecretOrProduce(),
                    deviceModel = getDeviceName(),
                    osVersion = getOSVersion(),
                    platform = getPlatform(),
                    version = getVersionName(),
                    refreshToken = sharedOperationUseCase.getRefreshToken()
                )
            return refreshAuthRequestBody
        }
    }

    fun generateSendDeviceTokenRequestBody(
        notificationToken: String,
    ): NotificationTokenRequestBody {

        context.apply {
            var notificationTokenRequestBody = NotificationTokenRequestBody(
                buildNumber = getVersionCode(),
                clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                clientUniqueId = sharedOperationUseCase.getClientUniqSecretOrProduce(),
                deviceModel = DeviceHelperExt.getDeviceName(),
                osVersion = getOSVersion(),
                platform = getPlatform(),
                version = getVersionName(),
                notificationToken = notificationToken
            )
            return notificationTokenRequestBody
        }
    }

    fun generateSaveBibleDeviceRequestBody(
    ): BibleSaveDeviceRequestBody {

        context.apply {
            var bibleId = sharedOperationUseCase.getSelectedBibleCode()
            if (bibleId.isNullOrEmpty()) {
                bibleId = "0"
            }
            var bibleSaveDeviceRequestBody = BibleSaveDeviceRequestBody(
                buildNumber = getVersionCode(),
                clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                clientUniqueId = sharedOperationUseCase.getClientUniqSecretOrProduce(),
                deviceModel = DeviceHelperExt.getDeviceName(),
                osVersion = getOSVersion(),
                platform = getPlatform(),
                version = getVersionName(),
                defaultBibleId = bibleId.toInt(),
                notificationToken = sharedOperationUseCase.getNotificationToken()
            )
            return bibleSaveDeviceRequestBody
        }
    }

    fun generateSettingsInfoRequestBody(): SettingsInfoRequestBody {
        context.apply {
            var settingsInfoRequestBody = SettingsInfoRequestBody(
                apiSecret = getApiKey(),
                buildNumber = getVersionCode(),
                clientSecret = sharedOperationUseCase.getClientSecretOrProduce(),
                platform = getPlatform()
            )
            return settingsInfoRequestBody
        }
    }

    private fun removeClientSecretForNewLogin() {
        sharedHelper.removeData(SharedPrefKey.Client_Secret)
    }

    private fun getApiKey(): String {
        return PrivateKeys.API_KEY
    }
}
