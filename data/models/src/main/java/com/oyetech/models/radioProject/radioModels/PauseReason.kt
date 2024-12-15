package com.oyetech.models.radioProject.radioModels

import androidx.annotation.Keep

/**
Created by Erdi Özbek
-18.11.2022-
-18:57-
 **/
@Keep
enum class PauseReason {
    NONE, BECAME_NOISY, FOCUS_LOSS, FOCUS_LOSS_TRANSIENT, METERED_CONNECTION, USER;
}
