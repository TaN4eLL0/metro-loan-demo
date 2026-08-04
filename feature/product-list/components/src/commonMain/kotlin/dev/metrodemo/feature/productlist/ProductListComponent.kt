package dev.metrodemo.feature.productlist

import com.arkivanov.decompose.value.Value

interface ProductListComponent {

    val model: Value<Model>

    fun onProductClick(productId: String)

    data class Model(
        val isLoading: Boolean = true,
        val products: List<ProductItem> = emptyList(),
    )

    data class ProductItem(
        val id: String,
        val title: String,
        val amountRange: String,
    )
}
