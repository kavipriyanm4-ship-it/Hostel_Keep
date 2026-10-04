plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hostelkeep"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hostelkeep"

        minSdk = 23
        targetSdk = 36

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

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {

    // -------------------------
    // Android
    // -------------------------

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // -------------------------
    // Jetpack Compose
    // -------------------------

    implementation(platform("androidx.compose:compose-bom:2025.09.01"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    debugImplementation("androidx.compose.ui:ui-tooling")

    // -------------------------
    // Navigation
    // -------------------------

    implementation("androidx.navigation:navigation-compose:2.9.4")

    // -------------------------
    // ViewModel
    // -------------------------

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

    // -------------------------
    // Kotlin Coroutines
    // -------------------------

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // -------------------------
    // Firebase
    // -------------------------

    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage")

    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging")

    // Firebase Cloud Functions
    implementation("com.google.firebase:firebase-functions")

    // -------------------------
    // Testing
    // -------------------------

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    androidTestImplementation(platform("androidx.compose:compose-bom:2025.09.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
