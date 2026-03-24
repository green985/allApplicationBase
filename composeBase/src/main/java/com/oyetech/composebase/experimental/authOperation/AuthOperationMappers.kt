package com.oyetech.composebase.experimental.authOperation

import com.oyetech.composebase.base.updateState
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import timber.log.Timber

fun AuthOperationVM.mapUserDataToState(userData: UserProfileProperty?) {
    if (userData == null) return

    Timber.d("AuthOperationVM mapUserDataToState: $userData")

    authOperationState.updateState {
        copy(
            userId = userData.userId,
            username = userData.username,
            age = userData.age,
            gender = userData.gender,
            biography = userData.biography,
            isAnonymous = userData.isAnonymous,
            isLogin = userData.isProfileCompletedForAuth(),
        )
    }
}

