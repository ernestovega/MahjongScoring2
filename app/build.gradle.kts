plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
}

android {
    compileSdk = 34
    namespace = "com.etologic.mahjongscoring2"

    defaultConfig {
        applicationId = "com.etologic.mahjongscoring2"
        minSdk = 23
        targetSdk = 34
        versionCode = 6
        versionName = "2.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    packaging {
        resources.excludes.add("META-INF/rxjava.properties")
    }
}

dependencies {
    //DAGGER
    implementation("com.google.dagger:dagger:2.48.1")
    kapt("com.google.dagger:dagger-compiler:2.48.1")
    implementation("com.google.dagger:dagger-android:2.48.1")
    implementation("com.google.dagger:dagger-android-support:2.48.1")
    kapt("com.google.dagger:dagger-android-processor:2.48.1")

    //ARCH COMPONENTS
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    ksp(libs.androidx.lifecycle.common.java8)

    //ROOM
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.rxjava2)

    //SUPPORT & DESIGN
    implementation(libs.appcompat)
    implementation(libs.androidx.browser)
    implementation(libs.material)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.core.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.annotation)

    //RX JAVA
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    //UTILS
    implementation(libs.commons.lang3)

    //IN APP REVIEW
    implementation(libs.review)
    implementation(libs.review.ktx)

    //TEST
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.truth)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

