plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvmToolchain(17)
    androidTarget()

    val xcfName = "designsystem"
    iosX64 { binaries.framework { baseName = xcfName } }
    iosArm64 { binaries.framework { baseName = xcfName } }
    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
                api(compose.material3)
                api(compose.components.resources)
            }
            kotlin.srcDirs("build/generated/compose/resourceGenerator/kotlin/commonMainResourceAccessors")
        }
        val commonTest by getting {
            dependencies { implementation(libs.kotlin.test) }
        }
        val androidMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
                api(compose.material3)
                api(compose.components.resources)
            }
            kotlin.srcDirs("build/generated/compose/resourceGenerator/kotlin/androidMainResourceAccessors")
        }
        val iosMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
                api(compose.material3)
                api(compose.components.resources)
            }
        }
    }
}

android {
    namespace = "com.vtempe.core.designsystem"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
compose.resources {
    packageOfResClass = "com.vtempe.core.designsystem"
    publicResClass = true
}