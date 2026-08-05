package dev.metrodemo.feature.productlist

import com.arkivanov.decompose.ComponentContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

/**
 * The per-screen graph — Metro's answer to our `scope<DefaultXComponent> { ... }` Koin block.
 *
 * `@ContributesTo(AppScope::class)` on the factory is what makes it appear on `AppGraph` without
 * `:shared` importing anything from this module: the equivalent of dropping `includes(...)`.
 * The `@Provides` parameters are the runtime inputs, replacing `factory { (params) -> ... }`.
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
