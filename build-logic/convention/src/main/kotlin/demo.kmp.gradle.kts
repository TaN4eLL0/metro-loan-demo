// Metro is applied per module via `alias(libs.plugins.metro)`, not from here: its Gradle plugin is
// compiled with Kotlin 2.2, which Gradle 8.13's embedded kotlin-dsl compiler refuses on the
// precompiled-script-plugin classpath.
plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

kotlin {
    jvmToolchain(17)

    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}

android {
    namespace = "dev.metrodemo" + project.path.replace(":", ".").replace("-", "")
    compileSdk = providers.gradleProperty("demo.compileSdk").get().toInt()
    defaultConfig {
        minSdk = providers.gradleProperty("demo.minSdk").get().toInt()
    }
}
