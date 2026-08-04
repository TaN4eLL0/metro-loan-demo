package dev.metrodemo.shared.di

import dev.metrodemo.core.common.Platform
import dev.metrodemo.core.common.format.MoneyFormatter
import dev.metrodemo.core.data.ProductsRepository
import dev.metrodemo.core.domain.CalculateTotalUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

/**
 * The single application-wide graph, shared by Android and iOS.
 *
 * Note what is *not* here: no list of feature modules, no binding declarations. Implementations
 * arrive from `:core:*` and `:feature:*` through Metro's aggregation, and the compiler fails the
 * build if any accessor below cannot be satisfied.
 */
@DependencyGraph(AppScope::class)
interface AppGraph {

    val platform: Platform

    val moneyFormatter: MoneyFormatter

    val productsRepository: ProductsRepository

    val calculateTotal: CalculateTotalUseCase

    @DependencyGraph.Factory
    fun interface Factory {

        fun create(@Provides platform: Platform): AppGraph
    }
}
