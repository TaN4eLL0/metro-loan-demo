package dev.metrodemo.core.data

interface ProductsRepository {

    suspend fun products(): List<Product>

    suspend fun product(id: String): Product
}
