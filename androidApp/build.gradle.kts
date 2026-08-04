plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.composeCompiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.metro)
}

android {
    namespace = "dev.metrodemo.android"
    compileSdk = providers.gradleProperty("demo.compileSdk").get().toInt()

    defaultConfig {
        applicationId = "dev.metrodemo.android"
        minSdk = providers.gradleProperty("demo.minSdk").get().toInt()
        targetSdk = providers.gradleProperty("demo.targetSdk").get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.shared)

    implementation(projects.feature.productList.ui)
    implementation(projects.feature.calculator.ui)
    implementation(projects.feature.summary.ui)
    implementation(projects.feature.result.ui)

    implementation(libs.androidx.activity.compose)
    implementation(libs.decompose)
    implementation(libs.decompose.compose)

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
}
