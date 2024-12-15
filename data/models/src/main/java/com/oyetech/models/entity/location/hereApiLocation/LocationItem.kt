package com.oyetech.models.entity.location.hereApiLocation

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class LocationItem(
    @Json(name = "access")
    var access: List<Acces?>? = listOf(),
    @Json(name = "address")
    var address: Address? = Address(),
    @Json(name = "categories")
    var categories: List<Category?>? = listOf(),
    @Json(name = "contacts")
    var contacts: List<Contact?>? = listOf(),
    @Json(name = "distance")
    var distance: Int? = 0,
    @Json(name = "id")
    var id: String? = "",
    @Json(name = "language")
    var language: String? = "",
    @Json(name = "ontologyId")
    var ontologyId: String? = "",
    @Json(name = "position")
    var position: Position? = Position(),
    @Json(name = "references")
    var references: List<Reference?>? = listOf(),
    @Json(name = "resultType")
    var resultType: String? = "",
    @Json(name = "title")
    var title: String? = ""
)