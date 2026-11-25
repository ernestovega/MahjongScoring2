import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.roborazzi)
}

android {

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk.debugSymbolLevel = "FULL"
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    compileSdk = 36

    defaultConfig {
        applicationId = "com.mahjongscoring.activities"
        minSdk = 23
        targetSdk = 36
        testInstrumentationRunner = "com.etologic.mahjongscoring2.HiltTestRunner"
        versionCode = 15
        versionName = "2.5.1"
    }

    namespace = "com.etologic.mahjongscoring2"

    ndkVersion = "28.2.13676358"

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    testOptions {
        unitTests {
            // Robolectric needs the merged resources/assets to inflate the real layouts.
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all { test ->
                test.maxHeapSize = "3g"
                // Deterministic environment for golden files and screenshots.
                test.systemProperty("user.timezone", "UTC")
                test.systemProperty("user.language", "en")
                test.systemProperty("user.country", "US")
                // Room schemas are read by the JVM migration tests.
                test.systemProperty("room.schemaDir", "$projectDir/schemas")
                // `-Pupdate.goldens=true` regenerates the text golden files under src/test/resources/golden.
                test.systemProperty("update.goldens", project.findProperty("update.goldens") ?: "false")
                test.systemProperty("goldens.dir", "$projectDir/src/test/resources/golden")
                test.systemProperty("screenshots.dir", "$projectDir/src/test/screenshots")
                // Optional Maven mirror for the Robolectric android-all jars (e.g. CI behind rate limits).
                project.findProperty("robolectric.dependency.repo.url")?.let {
                    test.systemProperty("robolectric.dependency.repo.url", it)
                }
            }
        }
    }
}

roborazzi {
    // Golden screenshots are committed to the repo so any visual change shows up in review.
    outputDir.set(file("src/test/screenshots"))
}

dependencies {

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.collection.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    implementation(libs.androidx.navigation.compose)
//    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.viewpager2)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.hilt.android)
    implementation(libs.hilt.android.navigation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material)
    implementation(libs.review.ktx)
    implementation(platform(libs.firebase.bom))

    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.android.compiler)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.navigation.test)
    androidTestImplementation(libs.androidx.room.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.truth)

    kspAndroidTest(libs.hilt.android.compiler)

    testImplementation(platform(libs.junit))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.junit4)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.androidx.espresso.core)
    testImplementation(libs.androidx.espresso.contrib)
    testImplementation(libs.androidx.navigation.test)
    testImplementation(libs.androidx.room.test)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.android.compiler)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }

    jvmToolchain(17)

    sourceSets.all {
        languageSettings {
            enableLanguageFeature("InlineClasses")
            enableLanguageFeature("ExplicitBackingFields")
        }
    }
}

ksp {
    arg("correctErrorTypes", "true")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}


tasks.withType<Test> {
    useJUnitPlatform()
}
