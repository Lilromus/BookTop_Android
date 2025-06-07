plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //Google services Gradle plugin
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.booktopprojekt"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.booktopprojekt"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        viewBinding = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Use the Firebase BOM to manage all Firebase versions
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    // Only declare firebase-analytics-ktx once (the BOM will pick the correct version)
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // If you still need Auth, Realtime Database, Crashlytics, keep those:
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics)
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}