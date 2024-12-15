package com.oyetech.audioPlayerHelper.alarm

/**
Created by Erdi Özbek
-10.12.2022-
-21:31-
 **/
/*
class AlarmReceiver : BroadcastReceiver() {

    val sharedOperationUseCase: SharedOperationUseCase by KoinJavaComponent.inject(
        SharedOperationUseCase::class.java
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

 */