plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)

}

android {

    namespace = "com.oyetech.composebase"

    compileSdk = libs.versions.compile.sdk.get().toInt()

    buildFeatures {
        compose = true
    }
    lint {
        disable.add("UnusedMaterial3ScaffoldPaddingParameter")
    }

}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.koin.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.media)

    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.paging.compose)
    implementation(libs.navigation.compose)
    implementation(libs.navigation.runtime.ktx)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    implementation(libs.glide.compose)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)

    debugImplementation(libs.androidx.ui.tooling)



    implementation(project(":subImpl:tools"))
    implementation(project(Modules.domain))
    implementation(project(Modules.glideModule))
    implementation(project(Modules.models))
    implementation(project(Modules.radioService))
    implementation(project(Modules.languageModule))
}
