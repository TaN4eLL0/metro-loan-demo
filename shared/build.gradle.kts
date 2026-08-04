import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("demo.kmp")
    alias(libs.plugins.metro)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.data)
            api(projects.core.domain)

            api(projects.feature.productList.components)
            api(projects.feature.calculator.components)
            api(projects.feature.summary.components)
            api(projects.feature.result.components)

            api(libs.decompose)
            api(libs.essenty.lifecycle)
            api(libs.essenty.stateKeeper)
            implementation(libs.coroutines.core)
            implementation(libs.serialization.json)
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true

            export(projects.core.common)
            export(projects.core.data)
            export(projects.feature.productList.components)
            export(projects.feature.calculator.components)
            export(projects.feature.summary.components)
            export(projects.feature.result.components)
            export(libs.decompose)
            export(libs.essenty.lifecycle)
        }
    }
}
