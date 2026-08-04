package dev.metrodemo.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.delay

/**
 * `@ContributesBinding` is the multi-module part of the experiment: nothing in `:shared` mentions
 * this class, yet it lands on `AppGraph` as the `ProductsRepository` binding purely through
 * Metro's compile-time aggregation.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class FakeProductsRepository : ProductsRepository {

    override suspend fun products(): List<Product> {
        delay(LOAD_DELAY_MS)
        return PRODUCTS
    }

    override suspend fun product(id: String): Product =
        PRODUCTS.first { it.id == id }

    private companion object {
        const val LOAD_DELAY_MS = 400L

        val PRODUCTS = listOf(
            Product(
                id = "pdl",
                title = "Préstamo rápido",
                minAmount = 1_000,
                maxAmount = 15_000,
                minTermDays = 7,
                maxTermDays = 30,
            ),
            Product(
                id = "ilg",
                title = "Préstamo a plazos",
                minAmount = 5_000,
                maxAmount = 60_000,
                minTermDays = 90,
                maxTermDays = 360,
            ),
        )
    }
}
