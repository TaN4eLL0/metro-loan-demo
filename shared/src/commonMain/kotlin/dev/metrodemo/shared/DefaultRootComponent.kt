package dev.metrodemo.shared

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import dev.metrodemo.core.common.LoanDraft
import dev.metrodemo.feature.calculator.CalculatorArgs
import dev.metrodemo.feature.calculator.CalculatorComponent
import dev.metrodemo.feature.calculator.CalculatorGraph
import dev.metrodemo.feature.calculator.CalculatorOutput
import dev.metrodemo.feature.productlist.ProductListComponent
import dev.metrodemo.feature.productlist.ProductListGraph
import dev.metrodemo.feature.productlist.ProductListOutput
import dev.metrodemo.feature.result.ResultArgs
import dev.metrodemo.feature.result.ResultComponent
import dev.metrodemo.feature.result.ResultGraph
import dev.metrodemo.feature.result.ResultOutput
import dev.metrodemo.feature.summary.SummaryArgs
import dev.metrodemo.feature.summary.SummaryComponent
import dev.metrodemo.feature.summary.SummaryGraph
import dev.metrodemo.feature.summary.SummaryOutput
import dev.metrodemo.shared.di.AppGraph
import dev.zacsweers.metro.asContribution
import kotlinx.serialization.Serializable

/**
 * The only place that holds [AppGraph] and the only place that knows how the features connect.
 *
 * Each child is built by asking the app graph for a screen graph — `asContribution<X.Factory>()`
 * resolves the factory Metro generated onto `AppGraph` from the feature module. Screen inputs and
 * the output callback go in as `@Provides` arguments.
 */
class DefaultRootComponent(
    componentContext: ComponentContext,
    private val appGraph: AppGraph,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.ProductList,
        key = "RootStack",
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            Config.ProductList -> RootComponent.Child.ProductList(createProductList(componentContext))
            is Config.Calculator -> RootComponent.Child.Calculator(createCalculator(config, componentContext))
            is Config.Summary -> RootComponent.Child.Summary(createSummary(config, componentContext))
            is Config.Result -> RootComponent.Child.Result(createResult(config, componentContext))
        }

    private fun createProductList(componentContext: ComponentContext): ProductListComponent =
        appGraph.asContribution<ProductListGraph.Factory>()
            .createProductListGraph(
                componentContext = componentContext,
                output = ProductListOutput { productId ->
                    navigation.push(Config.Calculator(productId = productId))
                },
            )
            .component

    private fun createCalculator(
        config: Config.Calculator,
        componentContext: ComponentContext,
    ): CalculatorComponent =
        appGraph.asContribution<CalculatorGraph.Factory>()
            .createCalculatorGraph(
                componentContext = componentContext,
                args = CalculatorArgs(productId = config.productId, prefill = config.prefill),
                output = object : CalculatorOutput {
                    override fun onDraftReady(draft: LoanDraft) {
                        navigation.push(Config.Summary(draft = draft))
                    }
                },
            )
            .component

    private fun createSummary(
        config: Config.Summary,
        componentContext: ComponentContext,
    ): SummaryComponent =
        appGraph.asContribution<SummaryGraph.Factory>()
            .createSummaryGraph(
                componentContext = componentContext,
                args = SummaryArgs(draft = config.draft),
                output = object : SummaryOutput {
                    override fun onEditRequested(draft: LoanDraft) {
                        backToCalculator(draft)
                    }

                    override fun onSubmitted(applicationId: String, draft: LoanDraft) {
                        navigation.push(Config.Result(applicationId = applicationId, draft = draft))
                    }
                },
            )
            .component

    private fun createResult(
        config: Config.Result,
        componentContext: ComponentContext,
    ): ResultComponent =
        appGraph.asContribution<ResultGraph.Factory>()
            .createResultGraph(
                componentContext = componentContext,
                args = ResultArgs(applicationId = config.applicationId, draft = config.draft),
                output = object : ResultOutput {
                    override fun onNewLoanRequested() {
                        navigation.replaceAll(Config.ProductList)
                    }

                    override fun onRepeatRequested(draft: LoanDraft) {
                        navigation.replaceAll(
                            Config.ProductList,
                            Config.Calculator(productId = draft.productId, prefill = draft),
                        )
                    }
                },
            )
            .component

    /**
     * Backward parameter passing: drop everything from the existing Calculator entry onwards and
     * re-enter it with the draft the user edited on Summary.
     */
    private fun backToCalculator(draft: LoanDraft) {
        navigation.navigate { stack ->
            stack.takeWhile { it !is Config.Calculator } +
                Config.Calculator(productId = draft.productId, prefill = draft)
        }
    }
}

@Serializable
private sealed interface Config {

    @Serializable
    data object ProductList : Config

    @Serializable
    data class Calculator(val productId: String, val prefill: LoanDraft? = null) : Config

    @Serializable
    data class Summary(val draft: LoanDraft) : Config

    @Serializable
    data class Result(val applicationId: String, val draft: LoanDraft) : Config
}
