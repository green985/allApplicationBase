package com.oyetech.models.wallpaperModels.requestBody

/**
Created by Erdi Özbek
-7.02.2024-
-13:21-
 **/

object SearchRequestConsts {

    var fileTypeQueryHeader = "type:"
    var fileTypeJpg = "jpg"
    var fileTypePng = "png"

    var orderTypeAsc = "asc"
    var orderTypeDesc = "desc"

    var likeQueryString = "like:"
    var categoryQueryString = "id:"

    val colorsList = arrayOf(
        0x660000, 0x990000, 0xcc0000, 0xcc3333, 0xea4c88, 0x993399, 0x663399, 0x333399,
        0x0066cc, 0x0099cc, 0x66cccc, 0x77cc33, 0x669900, 0x336600, 0x666600, 0x999900,
        0xcccc33, 0xffff00, 0xffcc33, 0xff9900, 0xff6600, 0xcc6633, 0x996633, 0x663300,
        0x999999, 0xcccccc, 0xffffff, 0x424153
    )

    var sortingMap = listOf(
        "random" to "Random",
        "views" to "Views",
        "toplist" to "Toplist",
        "relevance" to "Relevance",
        "date_added" to "Date Added",
        "favorites" to "Favorites",
    )

    fun getRatioRequestParam(ratioKey: String): String {
        if (ratioKey == "All Wide") {
            return ratioRequestLandscapeKey
        } else if (ratioKey == "All Portrait") {
            return ratioRequestPortraitKey
        }

        return ratioKey
    }

    private var ratioRequestLandscapeKey = "landscape"
    private var ratioRequestPortraitKey = "portrait"

    var resolutionMap = buildMap {
        put("Ultrawide", arrayOf("2560 × 1080", "3440 × 1440", "3840 × 1600").reversedArray())
        put(
            "16:9",
            arrayOf(
                "1280 × 720",
                "1600 × 900",
                "1920 × 1080",
                "2560 × 1440",
                "3840 × 2160"
            ).reversedArray()
        )
        put(
            "16:10",
            arrayOf(
                "1280 × 800",
                "1600 × 1000",
                "1920 × 1200",
                "2560 × 1600",
                "3840 × 2400"
            ).reversedArray()
        )
        put(
            "4:3",
            arrayOf(
                "1280 × 960",
                "1600 × 1200",
                "1920 × 1440",
                "2560 × 1920",
                "3840 × 2880"
            ).reversedArray()
        )
        put(
            "5:4",
            arrayOf(
                "1280 × 1024",
                "1600 × 1280",
                "1920 × 1536",
                "2560 × 2048",
                "3840 × 3072"
            ).reversedArray()
        )
    }
    val ratioGroups = hashMapOf<String, Array<String>>(
        "All" to arrayOf("All Wide", "All Portrait"),
        "Wide" to arrayOf("16x9", "16x10", "21x9", "32x9", "48x9"),
        "Ultrawide" to arrayOf("21x9", "32x9", "48x9"),
        "Portrait" to arrayOf("9x16", "10x16", "9x18"),
        "Square" to arrayOf("1x1", "3x2", "4x3", "5x4")
    )

    const val SORTING_RELEVANCE = "relevance"
    const val SORTING_RANDOM = "random"
    const val ORDER_DESC = "desc"
    const val ORDER_ASC = "asc"
    const val TOP_RANGE_1_DAY = "1d"
    const val TOP_RANGE_3_DAYS = "3d"
    const val TOP_RANGE_1_WEEK = "1w"
    const val TOP_RANGE_1_MONTH = "1M"
    const val TOP_RANGE_3_MONTHS = "3M"
    const val TOP_RANGE_6_MONTHS = "6M"
    const val TOP_RANGE_1_YEAR = "1y"
    const val TOP_RANGE_2_YEARS = "2y"
    const val TOP_RANGE_3_YEARS = "3y"
    const val TOP_RANGE_4_YEARS = "4y"
    const val TOP_RANGE_5_YEARS = "5y"
    const val TOP_RANGE_MAX = "max"
    const val FACET_TRUE = "true"
    const val FACET_FALSE = "false"
}

enum class CategoryTags {
    GENERAL, PEOPLE, ANIME
}

enum class PurityTags() {
    SFW, SKETCHY, NSFW
}