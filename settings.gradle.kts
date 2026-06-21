// File: settings.gradle.kts 
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // Объявляем ВСЕ версии, которые могут быть в проекте здесь:
        id("com.android.application") version "8.3.2" apply false
        id("com.android.library") version "8.3.2" apply false // <-- Добавление этой версии критично!
        id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    }
}

dependencyResolutionManagement { // <--- ВАЖНО: Это блок, где обычно добавляют репозитории для зависимостей
       repositories {
        google() // <-- И ОБЯЗАТЕЛЬНО ЗДЕСЬ! (Для библиотек, таких как Material)
        mavenCentral()
    }
}

// Затем продолжается обычный код settings.gradle.kts, например:
include(":app")
//include(":myapplication") // <-- Здесь мы просто "объявляем", что этот модуль должен существовать
