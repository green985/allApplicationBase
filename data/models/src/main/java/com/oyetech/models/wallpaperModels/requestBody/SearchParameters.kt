package com.oyetech.models.wallpaperModels.requestBody

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class SearchParameters(
    var q: String? = null,
    var fileTypeQuery: String? = null,
    var categories: List<String>? = null,
    var purity: Int? = null,
    var sorting: String? = null,
    var order: String? = null,
    var topRange: String? = null,
    var resolutions: String? = null,
    var ratios: List<String>? = null,
    var colors: String? = null,

    @Transient
    var page: Int? = null,

    var seed: String? = null,
    var atLeast: String? = null,
    var atleast_width: Int? = null,
    var atleast_height: Int? = null,
    var tags: Boolean? = null,
    var facet: String? = null,

    //TODO will be fixed with default values
    // defaults
    /*
    var puritySFW: Int = 1,
    var puritySketchy: Int = 0,
    var purityNSFW: Int = 0,



    var categoryGeneral: Int = 1,
    var categoryAnime: Int = 1,
    var categoryPeople: Int = 1,
     */


    var sortingKey: String? = null,
    var selectedRatioList: ArrayList<String> = arrayListOf(),

    var selectedResolution: String? = null,
    var isAtLeastSelected: Boolean? = null,

    var puritySFW: Int = 1,
    var puritySketchy: Int = 0,
    var purityNSFW: Int = 0,

    var categoryGeneral: Int = 1,
    var categoryAnime: Int = 1,
    var categoryPeople: Int = 1,

    var systemResolution: Pair<Int, Int>? = null,

    ) : Parcelable {

}

fun SearchParameters.toSearchMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()

    this.apply {
        var tmpQuery = q?.lowercase() ?: ""
        if (!fileTypeQuery.isNullOrEmpty()) {
            if (tmpQuery.isNotBlank()) {
                tmpQuery = tmpQuery.plus(":")
            }
            tmpQuery = tmpQuery.plus(fileTypeQuery)
        }

        if (tmpQuery.isNotBlank() == true) {
            map["q"] = tmpQuery
        }

        sorting?.let { map["sorting"] = it }
        order?.let { map["order"] = it }
        topRange?.let { map["topRange"] = it }


        colors?.let { map["colors"] = it.drop(1).lowercase() }
        page?.let { map["page"] = it }
        seed?.let { map["seed"] = it }
        atleast_width?.let { map["atleast_width"] = it }
        atleast_height?.let { map["atleast_height"] = it }
        tags?.let { map["tags"] = it }
        facet?.let { map["facet"] = it }


        if (!selectedResolution.isNullOrBlank()) {
            if (isAtLeastSelected == true) {
                map["atleast"] = selectedResolution!!.replace(" ", "")

            } else {
                map["resolutions"] = selectedResolution!!.replace(" ", "")
            }
        }

        if (!sortingKey.isNullOrBlank()) {
            if (sortingKey == "random") {
                // TODO generate random string for seed
                map["seed"] = generateRandomStringForWallpaperSeed()
            }

            map["sorting"] = sortingKey!!
        }

        if (selectedRatioList.isNotEmpty()) {
            var ratioKeyParam = ""
            selectedRatioList.map {
                ratioKeyParam += it + ","
            }
            ratioKeyParam = ratioKeyParam.dropLast(1)
            map["ratios"] = ratioKeyParam
        }

        resolutions?.let {
            Timber.d("asdnadljnasldjasd == " + it.replace(" ", ""))

            map["resolutions"] = it.replace(" ", "")
        }


        this.puritySearchStringCreator(map)
        this.categorySearchStringCreator(map)
        //purity?.let { map["purity"] = it }

        if (purityNSFW == 1) {
            map["apikey"] = "mxQULjlbilY6ZiAa4fvO96hcG1IMPuFX"
        }

    }

    Timber.d("mappppppppppp ===" + map.toString())
    return map

}

private fun generateRandomStringForWallpaperSeed(): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..6)
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}

fun SearchParameters.puritySearchStringCreator(map: HashMap<String, Any>) {
    var tmpString = "" + puritySFW + "" + this.puritySketchy + "" + this.purityNSFW

    if (tmpString != "000" && tmpString != "100") {
        map["purity"] = tmpString
    }
}

fun SearchParameters.categorySearchStringCreator(map: HashMap<String, Any>) {
    var tmpString = "" + this.categoryGeneral + "" + this.categoryAnime + "" + this.categoryPeople

    if (tmpString != "111") {
        map["categories"] = tmpString
    }
}

