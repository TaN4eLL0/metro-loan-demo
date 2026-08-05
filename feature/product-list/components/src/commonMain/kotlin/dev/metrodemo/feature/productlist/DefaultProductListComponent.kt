package dev.metrodemo.feature.productlist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.metrodemo.core.common.coroutines.componentScope
import dev.metrodemo.core.common.format.MoneyFormatter
import dev.metrodemo.core.data.ProductsRepository
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.launch

/**
 * `binding = binding<ProductListComponent>()` is required: delegating to `ComponentContext` makes it
 * a supertype too, so the contributed binding would otherwise be ambiguous.
 */
@SingleIn(ProductListScope::class)
@ContributesBinding(ProductListScope::class, binding = binding<ProductListComponent>())
@Inject
class DefaultProductListComponent(
    componentContext: ComponentContext,
    private val output: ProductListOutput,
    private val productsRepository: ProductsRepository,
    private val moneyFormatter: MoneyFormatter,
) : ProductListComponent, ComponentContext by componentContext {

    private val _model = MutableValue(ProductListComponent.Model())
    override val model: Value<ProductListComponent.Model> = _model

    private val scope = lifecycle.componentScope()

    init {
        scope.launch {
            val products = productsRepository.products()
            _model.value = ProductListComponent.Model(
                isLoading = false,
                products = products.map { product ->
                    ProductListComponent.ProductItem(
                        id = product.id,
                        title = product.title,
                        amountRange = "${moneyFormatter.format(product.minAmount)} — " +
                            moneyFormatter.format(product.maxAmount),
                    )
                },
            )
        }
    }

    override fun onProductClick(productId: String) {
        output.onProductSelected(productId)
    }
}
