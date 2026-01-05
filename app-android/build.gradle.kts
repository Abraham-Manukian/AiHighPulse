plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "com.example.aihighpulse"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.aihighpulse"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "API_BASE_URL", "\"https://api.example.com\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8081\"")
        }
        release {
            buildConfigField("String", "API_BASE_URL", "\"https://api.example.com\"")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose (AndroidX)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)

    // DI
    implementation(libs.koin.android)

    // SQLDelight driver (for creating driver in Android app)
    implementation(libs.sqldelight.driver.android)

    // Billing
    implementation(libs.play.billing)
}
