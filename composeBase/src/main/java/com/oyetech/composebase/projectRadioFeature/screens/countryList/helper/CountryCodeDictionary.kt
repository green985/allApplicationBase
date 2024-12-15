package com.oyetech.composebase.projectRadioFeature.screens.countryList.helper

import android.content.Context
import com.oyetech.composebase.R
import com.oyetech.models.radioProject.helperModels.country.Country
import com.oyetech.models.utils.moshi.convertFromRawJsonFile
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class CountryCodeDictionary {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )
    private val codeToCountry: MutableMap<String, String?> = HashMap()
    fun load() {
        val resources = context.resources
        val inputStream = resources.openRawResource(R.raw.countries)

        val countries = convertFromRawJsonFile<Country>(inputStream)
        if (countries != null) {
            for (country in countries) {
                codeToCountry[country.code.lowercase()] = country.name
            }
        } else {
            Timber.d("countryle name nulllll ===")

        }
    }

    fun getCountryByCode(code: String): String? {
        return codeToCountry[code.lowercase()]
    }
}