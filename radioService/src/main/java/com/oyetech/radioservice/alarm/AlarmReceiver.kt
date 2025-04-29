package com.oyetech.radioservice.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.useCases.AlarmOperationUseCase
import com.oyetech.domain.useCases.VolumeOperationUseCase
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils
import com.oyetech.radioservice.serviceUtils.ServiceConst
import com.oyetech.tools.AppUtil
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-10.12.2022-
-21:31-
 **/

class AlarmReceiver : BroadcastReceiver() {

    val sharedOperationUseCase: SharedOperationRepository by KoinJavaComponent.inject(
        SharedOperationRepository::class.java
    )

    val radioOperationUseCase: RadioOperationUseCase by KoinJavaComponent.inject(
        RadioOperationUseCase::class.java
    )
    val volumeOperationUseCase: VolumeOperationUseCase by KoinJavaComponent.inject(
        VolumeOperationUseCase::class.java
    )
    val alarmOperationUseCase: AlarmOperationUseCase by KoinJavaComponent.inject(
        AlarmOperationUseCase::class.java
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            AppUtil.dumpIntent(intent)
        }

        var alarmId = intent?.getIntExtra(ServiceConst.ALARM_ID, -1)

        if (alarmId == -1) {
            Timber.d("some problem....")
            return
        }

        PlayerServiceUtils.startService()
        playAlarmRadio()
    }

    private fun playAlarmRadio() {
        var alarm = sharedOperationUseCase.getAlarmm()
        if (alarm == null) {
            Timber.d("alarm model null")
            return
        }
        var radioModel = alarm?.selectedRadioStation
        if (radioModel == null) {
            Timber.d("radio moldel null")
            return
        }

        radioOperationUseCase.startPlayerForAlarm(radioModel)
        alarmOperationUseCase.setEnableAlarm(isEnable = false)
    }

}