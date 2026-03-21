plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

import java.util.Properties

android {
    namespace = "com.easyui.launcher"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.easyui.launcher"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    // Release signing — keys are read from local gradle.properties or CI environment variables.
    // DO NOT commit real keystore values to source control.
    // Set up by creating a keystore and adding these properties to your local gradle.properties:
    //   EASYUI_KEYSTORE_PATH=<absolute path to .jks>
    //   EASYUI_KEYSTORE_PASSWORD=<store password>
    //   EASYUI_KEY_ALIAS=<key alias>
    //   EASYUI_KEY_PASSWORD=<key password>
    signingConfigs {
        create("release") {
            val localSigningProperties = Properties().apply {
                val signingFile = rootProject.file("keystore.properties")
                if (signingFile.exists()) {
                    signingFile.inputStream().use(::load)
                }
            }
            fun readSigningValue(name: String): String? =
                System.getenv(name)
                    ?: (project.findProperty(name) as String?)
                    ?: localSigningProperties.getProperty(name)

            val keystorePath = readSigningValue("EASYUI_KEYSTORE_PATH")
            val storePassword = readSigningValue("EASYUI_KEYSTORE_PASSWORD")
            val keyAlias = readSigningValue("EASYUI_KEY_ALIAS")
            val keyPassword = readSigningValue("EASYUI_KEY_PASSWORD")

            if (keystorePath != null) {
                storeFile = rootProject.file(keystorePath)
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            val releaseSigningConfig = signingConfigs.getByName("release")
            if (releaseSigningConfig.storeFile != null) {
                signingConfig = releaseSigningConfig
            }
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    // Android App Bundle (AAB) is the required upload format for Play Store.
    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:platform"))
    implementation(project(":feature:home"))
    implementation(project(":feature:apps"))
    implementation(project(":feature:caregiver"))
    implementation(project(":feature:onboarding"))

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.compose.ui:ui:1.7.1")
    implementation("androidx.compose.foundation:foundation:1.7.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.1")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.1")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.1")
}
