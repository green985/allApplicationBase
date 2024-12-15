package com.oyetech.models.postBody.bibles

import androidx.annotation.Keep
import com.squareup.moshi.Json

/**
Created by Erdi Özbek
-6.12.2023-
-14:05-
 **/

@Keep
data class BibleChapterReadOperationRequestBody(
    @Json(name = "chapterId") var id: Int = 0,
)