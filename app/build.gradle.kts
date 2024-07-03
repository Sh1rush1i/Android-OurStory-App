import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hilt.gradle.plugin)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.submis.ourstory"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.submis.ourstory"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load properties from the local.properties file
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream ->
                properties.load(stream)
            }
        }

        // Read the dicoding story url api for story from local properties
        val dicodingUrl: String = properties.getProperty("DICODING_URL")

        // Add the build config field
        buildConfigField("String", "DICODING_URL", "\"$dicodingUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Dummy check
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            buildConfigField("String", "BASE_URL", "\"https://api.debug.example.com\"")
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    //Main Default Imp
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //Testing Imp
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.android.test.runner)
    androidTestImplementation(libs.android.test.ext.junit)

    //Camera Imp
    implementation(libs.camera.view)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.compressor)

    //User Interface Imp
    implementation(libs.glide)
    implementation(libs.lottie)
    implementation(libs.swipe.refresh.layout)
    implementation(libs.paging.runtime.ktx) // Paging

    //Navigation Imp
    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment.ktx)

    //Google Maps
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    //Retrofit Imp
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.okhttp.logging.interceptor)

    // Lifecycle Imp
    implementation(libs.lifecycle.viewModel.ktx)
    implementation(libs.lifecycle.liveData.ktx)

    // DataStore Imp
    implementation(libs.datastore.preferences)

    // Room Imp
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)

    // AndroidX startup Imp
    implementation (libs.androidx.startup.runtime)

    // Espresso helper testing Imp
    implementation(libs.espresso.idling.resource)

    // Hilt Imp
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)

}