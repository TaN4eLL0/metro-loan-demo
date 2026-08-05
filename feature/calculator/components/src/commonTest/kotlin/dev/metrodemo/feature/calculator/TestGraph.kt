package dev.metrodemo.feature.calculator

import dev.metrodemo.core.common.Platform
import dev.metrodemo.core.data.FakeProductsRepository
import dev.metrodemo.core.data.Product
import dev.metrodemo.core.data.ProductsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides

/**
 * Test doubles in Metro are swapped with `replaces`, not by overriding a module the way
 * `startKoin { modules(fakeModule) }` does. The replacement is checked at compile time: name a class
 * that is not actually contributed and the test source set stops compiling.
 */
@ContributesBinding(AppScope::class, replaces = [FakeProductsRepository::class])
@Inject
class StubProductsRepository : ProductsRepository {

    override suspend fun products(): List<Product> = listOf(STUB_PRODUCT)

    override suspend fun product(id: String): Product = STUB_PRODUCT

    private companion object {
        val STUB_PRODUCT = Product(
            id = "stub",
            title = "Stub product",
            minAmount = 1_000,
            maxAmount = 10_000,
            minTermDays = 10,
            maxTermDays = 20,
        )
    }
}

/**
 * A graph declared in the test source set. Because it is an `AppScope` graph it picks up the real
 * contributions from `:core:*` plus [StubProductsRepository] from here, and the compiler assembles
 * `CalculatorGraph.Factory` onto it exactly as it does for the production `AppGraph`.
 */
@DependencyGraph(AppScope::class)
interface TestGraph {

    @DependencyGraph.Factory
    fun interface Factory {

        fun create(@Provides platform: Platform): TestGraph
    }
}
