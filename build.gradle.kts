// КОРНЕВОЙ build.gradle.kts — ТОЛЬКО управление плагинами, БЕЗ применения kotlin
plugins {
    id("com.android.application") version "9.2.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    alias(libs.plugins.kotlin.compose) apply false
}
//
//tasks.register<Delete>("clean") {
//    delete(rootProject.buildDir)
//}