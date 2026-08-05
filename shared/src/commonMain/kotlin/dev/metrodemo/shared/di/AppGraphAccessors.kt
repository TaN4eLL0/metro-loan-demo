package dev.metrodemo.shared.di

import com.arkivanov.decompose.ComponentContext
import dev.metrodemo.core.common.Platform
import dev.metrodemo.shared.DefaultRootComponent
import dev.metrodemo.shared.RootComponent
import dev.zacsweers.metro.createGraphFactory

/**
 * The typed entry points that Swift and Android both go through.
 *
 * This replaces the `keyPaths` allowlist plus `@LazyKoin` we have in android-mx: there is no
 * `get<T>()` to mistype, so a caller cannot ask for something the graph does not have.
 */
fun createAppGraph(platform: Platform): AppGraph =
    createGraphFactory<AppGraph.Factory>().create(platform = platform)

fun AppGraph.createRootComponent(componentContext: ComponentContext): RootComponent =
    DefaultRootComponent(componentContext = componentContext, appGraph = this)
