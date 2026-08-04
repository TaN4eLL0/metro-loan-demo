plugins {
    id("demo.kmp")
    alias(libs.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.decompose)
            implementation(projects.core.domain)
            implementation(libs.coroutines.core)
        }
    }
}
