package com.oyetech.models.postBody.bibles

import androidx.annotation.Keep
import com.squareup.moshi.Json

/**
Created by Erdi Özbek
-23.11.2023-
-18:07-
 **/

@Keep
data class BibleBookReadOperationRequestBody(
    @Json(name = "bookId") var id: Int = 0,
)