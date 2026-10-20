package dev.metrodemo.feature.productlist

import com.arkivanov.decompose.ComponentContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

/**
 * `@ContributesTo(AppScope::class)` on the factory is what makes this screen graph reachable from
 * `AppGraph` without `:shared` importing anything from this module.
 */
@GraphExtension(ProductListScope::class)
interface ProductListGraph {

    val component: ProductListComponent

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {

        fun createProductListGraph(
            @Provides componentContext: ComponentContext,
            @Provides output: ProductListOutput,
        ): ProductListGraph
    }
}
