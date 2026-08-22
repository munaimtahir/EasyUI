plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.easyui.senior"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.easyui.senior"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("EASYUI_RELEASE_KEYSTORE_PATH") ?: project.findProperty("EASYUI_RELEASE_KEYSTORE_PATH") as? String
            val keystorePassword = System.getenv("EASYUI_RELEASE_KEYSTORE_PASSWORD") ?: project.findProperty("EASYUI_RELEASE_KEYSTORE_PASSWORD") as? String
            val keyAlias = System.getenv("EASYUI_RELEASE_KEY_ALIAS") ?: project.findProperty("EASYUI_RELEASE_KEY_ALIAS") as? String
            val keyPassword = System.getenv("EASYUI_RELEASE_KEY_PASSWORD") ?: project.findProperty("EASYUI_RELEASE_KEY_PASSWORD") as? String

            if (keystorePath != null && file(keystorePath).exists() && keystorePassword != null && keyAlias != null && keyPassword != null) {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            val devBackendUrl = System.getenv("EASYUI_DEV_BACKEND_URL") ?: project.findProperty("EASYUI_DEV_BACKEND_URL") as? String ?: "http://10.0.2.2:8088"
            buildConfigField("String", "BACKEND_BASE_URL", "\"$devBackendUrl\"")
            buildConfigField("String", "ENVIRONMENT", "\"DEVELOPMENT\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            val prodBackendUrl = System.getenv("EASYUI_PROD_BACKEND_URL") ?: project.findProperty("EASYUI_PROD_BACKEND_URL") as? String ?: "https://api.easyui.vexel.pk"
            buildConfigField("String", "BACKEND_BASE_URL", "\"$prodBackendUrl\"")
            buildConfigField("String", "ENVIRONMENT", "\"PRODUCTION\"")

            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null) {
                signingConfig = releaseSigning
            }
        }
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.material)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
