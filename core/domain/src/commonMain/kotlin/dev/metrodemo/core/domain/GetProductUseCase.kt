package dev.metrodemo.core.domain

import dev.metrodemo.core.data.Product
import dev.metrodemo.core.data.ProductsRepository
import dev.zacsweers.metro.Inject

@Inject
class GetProductUseCase(
    private val repository: ProductsRepository,
) {

    suspend operator fun invoke(productId: String): Product = repository.product(productId)
}
