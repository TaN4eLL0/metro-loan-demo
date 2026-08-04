plugins {
    `kotlin-dsl`
}

kotlin {
    // Metro's Gradle plugin is published for Java 21.
    jvmToolchain(21)
}

dependencies {
    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.android)
    implementation(libs.gradlePlugin.compose)
    implementation(libs.gradlePlugin.composeCompiler)
}
