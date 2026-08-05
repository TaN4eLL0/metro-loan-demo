package dev.metrodemo.shared

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import dev.metrodemo.feature.calculator.CalculatorComponent
import dev.metrodemo.feature.productlist.ProductListComponent
import dev.metrodemo.feature.result.ResultComponent
import dev.metrodemo.feature.summary.SummaryComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {

        class ProductList(val component: ProductListComponent) : Child

        class Calculator(val component: CalculatorComponent) : Child

        class Summary(val component: SummaryComponent) : Child

        class Result(val component: ResultComponent) : Child
    }
}
