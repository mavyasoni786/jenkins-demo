plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("jacoco")
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



jacoco {
    toolVersion = "0.8.10"
}

val excludes =
    listOf(
        "**/androidTest/**",
        "**/test/**",
        "**/databinding/*Binding.*",
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        // butterKnife
        "**/*$*ViewInjector*.*",
        "**/*$*ViewBinder*.*",
        "**/Lambda$*.class",
        "**/Lambda.class",
        "**/*Lambda.class",
        "**/*Lambda*.class",
        "**/*_MembersInjector.class",
        "**/Dagger*Component*.*",
        "**/*Module_*Factory.class",
        "**/di/module/*",
        "**/*_Factory*.*",
        "**/*Module*.*",
        "**/*Dagger*.*",
        "**/*Hilt*.*",
        // kotlin
        "**/*MapperImpl*.*",
        "**/*$*ViewInjector*.*",
        "**/*$*ViewBinder*.*",
        "**/BuildConfig.*",
        "**/*Component*.*",
        "**/*BR*.*",
        "**/Manifest*.*",
        "**/*Lambda$*.*",
        "**/*Companion*.*",
        "**/*Module*.*",
        "**/*Dagger*.*",
        "**/*Hilt*.*",
        "**/*MembersInjector*.*",
        "**/*_MembersInjector.class",
        "**/*_Factory*.*",
        "**/*_Provide*Factory*.*",
        "**/*Extensions*.*",
    )

fun createVariantCoverage(variant: com.android.build.gradle.api.BaseVariant) {
    val variantName = variant.name
    val testTaskName = "test${variantName.capitalize()}UnitTest"

    // Add unit test coverage tasks
    tasks.register<JacocoReport>("${testTaskName}Coverage") {
        group = "Reporting"
        description = "Generate Jacoco coverage reports for the ${variantName.capitalize()} build."
        dependsOn(tasks.named(testTaskName))

        reports {
            xml.required = true
        }

        val javaClasses =
            fileTree(
                mapOf(
                    "dir" to variant.javaCompileProvider.get().destinationDir,
                    "excludes" to excludes,
                ),
            )
        val kotlinClasses =
            fileTree(
                mapOf(
                    "dir" to "$buildDir/tmp/kotlin-classes/$variantName",
                    "excludes" to excludes,
                ),
            )

        classDirectories.setFrom(files(javaClasses, kotlinClasses))

        sourceDirectories.setFrom(
            files(
                "$projectDir/src/main/java",
                "$projectDir/src/$variantName/java",
                "$projectDir/src/main/kotlin",
                "$projectDir/src/$variantName/kotlin",
            ),
        )

        executionData.setFrom(files("build/jacoco/$testTaskName.exec"))

        doLast {
            val m =
                File("$buildDir/reports/jacoco/${testTaskName}Coverage/html/index.html")
                    .readText()
                    .let {
                        print(it)
                        Regex("Total[^%]*>(\\d{1,3}%)").find(it)?.groupValues?.get(1)
                    }
            if (m != null) {
                println("Test coverage: $m")
            }
        }
    }

    // Add unit test coverage verification tasks
    tasks.register<JacocoCoverageVerification>("${testTaskName}CoverageVerification") {
        group = "Reporting"
        description = "Verifies Jacoco coverage for the ${variantName.capitalize()} build."
        dependsOn("${testTaskName}Coverage")

        violationRules {
            rule {
                limit {
                    minimum = BigDecimal(0)
                }
            }
            rule {
                element = "BUNDLE"
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = BigDecimal(0.70)
                }
            }
        }

        val javaClasses =
            fileTree(
                mapOf(
                    "dir" to variant.javaCompileProvider.get().destinationDir,
                    "excludes" to excludes,
                ),
            )
        val kotlinClasses =
            fileTree(
                mapOf(
                    "dir" to "$buildDir/tmp/kotlin-classes/$variantName",
                    "excludes" to excludes,
                ),
            )

        classDirectories.setFrom(files(javaClasses, kotlinClasses))

        sourceDirectories.setFrom(
            files(
                "$projectDir/src/main/java",
                "$projectDir/src/$variantName/java",
                "$projectDir/src/main/kotlin",
                "$projectDir/src/$variantName/kotlin",
            ),
        )

        executionData.setFrom(files("build/jacoco/$testTaskName.exec"))
    }
}
afterEvaluate {
    android.applicationVariants
        .configureEach(::createVariantCoverage)
}