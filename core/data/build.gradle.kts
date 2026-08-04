plugins {
    id("demo.kmp")
    alias(libs.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(libs.coroutines.core)
        }
    }
}
