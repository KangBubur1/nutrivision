plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.nutrivision"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.nutrivision"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.glide) // For image loading and caching
    implementation(libs.retrofit) // For API calls
    implementation(libs.logging.interceptor) // For logging HTTP requests and responses
    implementation(libs.converter.gson) // For converting JSON to Kotlin objects


    // Navigation components
    implementation(libs.androidx.navigation.fragment) // Navigation fragment components
    implementation(libs.androidx.navigation.fragment.ktx) // KTX extensions for navigation fragment
    implementation(libs.androidx.navigation.ui) // Navigation UI components
    implementation(libs.androidx.navigation.ui.ktx) // KTX extensions for navigation UI

    // Room database (Local)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.room.compiler)

    // Maps
    implementation(libs.play.services.maps)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}