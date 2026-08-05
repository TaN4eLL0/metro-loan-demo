plugins {
    id("demo.kmp")
    alias(libs.plugins.metro)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.essenty.lifecycle)
            api(libs.coroutines.core)
            implementation(libs.serialization.core)
        }
    }
}
