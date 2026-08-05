package dev.metrodemo.feature.result

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.metrodemo.core.common.coroutines.componentScope
import dev.metrodemo.core.common.format.MoneyFormatter
import dev.metrodemo.core.domain.GetProductUseCase
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.launch

@SingleIn(ResultScope::class)
@ContributesBinding(ResultScope::class, binding = binding<ResultComponent>())
@Inject
class DefaultResultComponent(
    componentContext: ComponentContext,
    private val args: ResultArgs,
    private val output: ResultOutput,
    private val getProduct: GetProductUseCase,
    private val moneyFormatter: MoneyFormatter,
) : ResultComponent, ComponentContext by componentContext {

    private val _model = MutableValue(
        ResultComponent.Model(
            applicationId = args.applicationId,
            amountText = moneyFormatter.format(args.draft.amount),
        ),
    )
    override val model: Value<ResultComponent.Model> = _model

    private val scope = lifecycle.componentScope()

    init {
        scope.launch {
            _model.value = _model.value.copy(productTitle = getProduct(args.draft.productId).title)
        }
    }

    override fun onNewLoanClick() {
        output.onNewLoanRequested()
    }

    override fun onRepeatClick() {
        output.onRepeatRequested(args.draft)
    }
}
