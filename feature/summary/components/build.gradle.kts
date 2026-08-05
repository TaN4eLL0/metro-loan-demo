plugins {
    id("demo.kmp")
    alias(libs.plugins.metro)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.decompose)
            implementation(projects.core.domain)
            implementation(libs.coroutines.core)
            implementation(libs.serialization.core)
        }
    }
}
