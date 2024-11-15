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
        mlModelBinding = true
    }
}

dependencies {

//    WebView
    implementation("androidx.webkit:webkit:1.12.1")

    // tensorflow
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite.task.vision.play.services)
    implementation(libs.play.services.tflite.support)
    implementation(libs.play.services.tflite.gpu)
    implementation(libs.tensorflow.lite.gpu)
    implementation (libs.tensorflow.lite)
    implementation(("org.tensorflow:tensorflow-lite-support:0.4.4"))


    val cameraxVersion = "1.3.4"
    // Camera
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation ("androidx.camera:camera-core:$cameraxVersion")


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