plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.easyui.core.ui"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {
    implementation("androidx.compose.foundation:foundation:1.7.1")
    implementation("androidx.compose.ui:ui:1.7.1")
    implementation("androidx.compose.ui:ui-graphics:1.7.1")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
}
