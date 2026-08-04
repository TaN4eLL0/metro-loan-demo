rootProject.name = "metro-loan-demo"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":core:common",
    ":core:data",
    ":core:domain",

    ":feature:product-list:components",
    ":feature:product-list:ui",
    ":feature:calculator:components",
    ":feature:calculator:ui",
    ":feature:summary:components",
    ":feature:summary:ui",
    ":feature:result:components",
    ":feature:result:ui",

    ":shared",
    ":androidApp",
)
