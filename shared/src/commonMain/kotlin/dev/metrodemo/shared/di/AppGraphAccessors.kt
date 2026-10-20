package dev.metrodemo.shared.di

import com.arkivanov.decompose.ComponentContext
import dev.metrodemo.core.common.Platform
import dev.metrodemo.shared.DefaultRootComponent
import dev.metrodemo.shared.RootComponent
import dev.zacsweers.metro.createGraphFactory

fun createAppGraph(platform: Platform): AppGraph =
    createGraphFactory<AppGraph.Factory>().create(platform = platform)

fun AppGraph.createRootComponent(componentContext: ComponentContext): RootComponent =
    DefaultRootComponent(componentContext = componentContext, appGraph = this)
