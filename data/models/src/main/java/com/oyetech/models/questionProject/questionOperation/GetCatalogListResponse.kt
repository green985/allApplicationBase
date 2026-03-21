package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class GetCatalogListResponse(
    val catalogs: List<CatalogItem>,
)

@Keep
data class GetCatalogListRequest(
    val queryText: String,
)
