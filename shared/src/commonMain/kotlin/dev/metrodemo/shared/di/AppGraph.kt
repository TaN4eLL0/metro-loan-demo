package dev.metrodemo.shared.di

import dev.metrodemo.core.common.Platform
import dev.metrodemo.core.common.analytics.AnalyticsSink
import dev.metrodemo.core.common.format.MoneyFormatter
import dev.metrodemo.core.data.storage.KeyValueStorage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

/**
 * The single application-wide graph, shared by Android and iOS.
 *
 * Note what is *not* here: no list of feature modules, no binding declarations, and no mention of
 * the per-screen graphs. Everything arrives from `:core:*` and `:feature:*` through Metro's
 * aggregation, and the compiler fails the build if anything cannot be satisfied.
 */
@DependencyGraph(AppScope::class)
interface AppGraph {

    val platform: Platform

    val moneyFormatter: MoneyFormatter

    val analyticsSinks: Set<AnalyticsSink>

    @Named("memory")
    val memoryStorage: KeyValueStorage

    @Named("persistent")
    val persistentStorage: KeyValueStorage

    @DependencyGraph.Factory
    fun interface Factory {

        fun create(@Provides platform: Platform): AppGraph
    }
}
