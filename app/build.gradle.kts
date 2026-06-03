import java.util.Properties

plugins {
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.2.0"

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.moris.ergo"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.moris.ergo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")

        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }

        val stripeKey = properties.getProperty("STRIPE_PUBLISHABLE_KEY") ?: ""
        buildConfigField("String", "STRIPE_PUBLISHABLE_KEY", "\"$stripeKey\"")

        val apiUrl = properties.getProperty("EROG_SERVER_API") ?: ""
        buildConfigField("String", "EROG_SERVER_API", "\"$apiUrl\"")
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(
                org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.stripe.android)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    implementation(libs.osmdroid.android)

    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.auth)

    implementation(libs.hilt.android)
    implementation(libs.androidx.browser)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.slf4j.android)
    implementation(libs.ktor.client.logging)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.metadata.jvm)
    implementation(libs.compose.material.icons)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}