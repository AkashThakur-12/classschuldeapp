import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.akash.classschuldeapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.akash.classschuldeapp"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(FileInputStream(localPropertiesFile))
        }
        val geminiKey = properties.getProperty("GEMINI_API_KEY") ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
        val cloudinaryCloudName = properties.getProperty("CLOUDINARY_CLOUD_NAME") ?: ""
        val cloudinaryApiKey = properties.getProperty("CLOUDINARY_API_KEY") ?: ""
        val cloudinaryApiSecret = properties.getProperty("CLOUDINARY_API_SECRET") ?: ""
        val backendUrl = properties.getProperty("BACKEND_URL") ?: ""
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"$cloudinaryCloudName\"")
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"$cloudinaryApiKey\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"$cloudinaryApiSecret\"")
        buildConfigField("String", "BASE_URL", "\"$backendUrl\"")
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.android.gms:play-services-auth:21.4.0")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("com.github.Kishan8548:EasyCardsLibrary:1.0.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Gemini AI
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // OkHttp for Cloudinary upload
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}


