package com.oyetech.remote.questionRemote

import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.remote.helper.interceptGenericResponseTrueForm
import kotlinx.coroutines.flow.Flow

class QuestionSupabaseDataSource(private val questionSupabaseApi: QuestionSupabaseApi) {

    fun createQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.createQuestion(question)
        }
    }

    fun updateQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.updateQuestion(question)
        }
    }

    fun registerGoogleUser(googleUserResponseData: GoogleUserPostData): Flow<UserProfileProperty> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.registerGoogleUser(googleUserResponseData)
        }
    }

    fun updateUser(userProfileProperty: UserProfileProperty): Flow<UserProfileProperty> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.updateUser(userProfileProperty)
        }
    }

    fun getUserWithToken(getUserWithTokenBody: GetUserWithTokenBody): Flow<UserProfileProperty> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.getUserWithToken(getUserWithTokenBody)
        }
    }
}
