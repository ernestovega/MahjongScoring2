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
    implementation("androidx.room:room-runtime:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    implementation("androidx.room:room-rxjava2:2.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    ksp("androidx.lifecycle:lifecycle-common-java8:2.6.2")

    //SUPPORT & DESIGN
    implementation(libs.appcompat)
    implementation("androidx.browser:browser:1.6.0")
    implementation(libs.material)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation(libs.core.ktx)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.annotation:annotation:1.7.0")

    //RX JAVA
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")

    //UTILS
    implementation("org.apache.commons:commons-lang3:3.14.0")

    //IN APP REVIEW
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

    //TEST
    testImplementation(libs.junit)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("com.google.truth:truth:1.1.5")
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

