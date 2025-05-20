plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val localKeyAlias = System.getenv()["APP_KEYSTORE_CREDENTIALS_USR"] ?: extra["APP_KEYSTORE_ALIAS"].toString()
val localKeyPassword = System.getenv()["APP_KEYSTORE_CREDENTIALS_PSW"] ?: extra["APP_KEYSTORE_PASSWORD"].toString()
val localStoreFilename = "../.signing/debug.keystore"
val localStorePassword = System.getenv()["APP_KEYSTORE_CREDENTIALS_PSW"] ?: extra["APP_KEYSTORE_PASSWORD"].toString()


android {
    namespace = "com.ms.jenkins"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ms.jenkins"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("jenkins") {
            try {
                keyAlias = localKeyAlias
                keyPassword = localKeyPassword
                storeFile = file(localStoreFilename)
                storePassword = localStorePassword
            } catch (ignored: Exception) {
                throw InvalidUserDataException(
                    "You should define APP_KEYSTORE_ALIAS and APP_KEYSTORE_PASSWORD in local.properties.",
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("jenkins")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }



}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}