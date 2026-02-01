plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.oyetech.tools"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {

    api(Libraries.koin)
    implementation(Libraries.koinCompose)
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:${Versions.koin}"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-androidx-compose-navigation")


    implementation(project(Modules.models))
    implementation(KotlinLibraries.kotlin)
    implementation(Libraries.timber)

    api(Libraries.koin)

    implementation(KotlinLibraries.kotlin)
    implementation(Libraries.gson)
    implementation(Libraries.moshi)
    implementation(Libraries.moshiKotlin)
    implementation(Libraries.roomRunTime)

}