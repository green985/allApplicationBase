buildscript {
    extra.apply {
        set("kotlinVersion", "2.0.20")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.7.0")

//         classpath("com.android.tools:r8:8.5.10")

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.nav}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
        classpath("org.jetbrains.kotlinx:kover:0.5.0")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.2")

    }

}

ksp {

    arg("moshi.generateProguardRules", "false")
}
plugins {
    id("com.android.application") version "8.7.0" apply false
    id("com.android.library") version "8.7.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20" apply false


    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("org.lsposed.lsparanoid") version "0.5.2" apply false
    id("io.gitlab.arturbosch.detekt").version("1.20.0-RC2")
    id("com.android.test") version "8.7.0" apply false
    id("androidx.baselineprofile") version "1.2.4" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25"

}
apply(plugin = "kover")


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

