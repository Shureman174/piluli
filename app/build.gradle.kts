plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
   // compileSdkVersion(34) // <-- Актуальная версия SDK
    compileSdk =34
    defaultConfig {
        applicationId ="com.example.myapp"
//        minSdkVersion(21)
        minSdk =21
       // targetSdkVersion (34) // <-- Актуальная версия SDK
        targetSdk =34 // <-- Актуальная версия SDK
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.example.myapp" // ИСПРАВЛЕНО: Добавлено обязательное пространство имён для AGP

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21 // Явное указание на Java 21
        targetCompatibility = JavaVersion.VERSION_21
    }

    // Дополнительно, для Kotlin (хотя это часто обрабатывается автоматически)
    kotlinOptions {
        jvmTarget = "21"
    }
    // ***************************************


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    // Используйте latest версии!
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.material:material:1.11.0")
}