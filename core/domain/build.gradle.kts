plugins {
    id("demo.kmp")
    alias(libs.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.data)
            implementation(libs.coroutines.core)
        }
    }
}
