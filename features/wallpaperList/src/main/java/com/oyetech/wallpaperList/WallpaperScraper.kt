package com.oyetech.wallpaperList

/**
Created by Erdi Özbek
-27.02.2024-
-16:30-
 **/

/*

    fun scrapper() {

        val jsonData =
            extractDataFromDiv()
        println(jsonData.toString())
    }

 */
/*
var denemeHtml = """
""".trimIndent()

fun extractDataFromDiv(): String {
    var html = denemeHtml
    val doc = Jsoup.parse(html)

    var listName = doc.select("span.taglist-name")
    var listUrl = doc.select(".taglist-tagmain .taglist-name a")
    var listCategory = doc.select("span.taglist-category")

    var jsonArray = JSONArray()

    listName.forEachIndexed { index, element ->
        var jsonObject = JSONObject()
        jsonObject.put("tagName", element.text())
        jsonObject.put("category", listCategory.get(index).text())

        var tagId = exractTagId(listUrl.get(index).attr("href"))

        jsonObject.put("tagId", tagId)
        jsonArray.put(jsonObject)

    }



    return jsonArray.toString()
}

private fun exractTagId(url: String): String? {
    val pattern = Regex("""/tag/(\d+)""")
    val matchResult = pattern.find(url)
    val tagNumber = matchResult?.groups?.get(1)?.value
    return tagNumber
}

 */