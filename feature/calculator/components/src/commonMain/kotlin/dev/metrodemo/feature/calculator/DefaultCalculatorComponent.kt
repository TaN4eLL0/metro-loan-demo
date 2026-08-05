package dev.metrodemo.feature.calculator

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.metrodemo.core.common.LoanDraft
import dev.metrodemo.core.common.coroutines.componentScope
import dev.metrodemo.core.common.format.MoneyFormatter
import dev.metrodemo.core.domain.CalculateTotalUseCase
import dev.metrodemo.core.domain.GetProductUseCase
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.launch

@SingleIn(CalculatorScope::class)
@ContributesBinding(CalculatorScope::class, binding = binding<CalculatorComponent>())
@Inject
class DefaultCalculatorComponent(
    componentContext: ComponentContext,
    private val args: CalculatorArgs,
    private val output: CalculatorOutput,
    private val getProduct: GetProductUseCase,
    private val calculateTotal: CalculateTotalUseCase,
    private val moneyFormatter: MoneyFormatter,
) : CalculatorComponent, ComponentContext by componentContext {

    private val _model = MutableValue(CalculatorComponent.Model())
    override val model: Value<CalculatorComponent.Model> = _model

    private val scope = lifecycle.componentScope()

    init {
        scope.launch {
            val product = getProduct(args.productId)
            _model.value = withDerivedText(
                CalculatorComponent.Model(
                    productTitle = product.title,
                    amount = args.prefill?.amount ?: product.minAmount,
                    minAmount = product.minAmount,
                    maxAmount = product.maxAmount,
                    termDays = args.prefill?.termDays ?: product.minTermDays,
                    minTermDays = product.minTermDays,
                    maxTermDays = product.maxTermDays,
                    isContinueEnabled = true,
                ),
            )
        }
    }

    override fun onAmountChange(amount: Int) {
        _model.value = withDerivedText(_model.value.copy(amount = amount))
    }

    override fun onTermChange(termDays: Int) {
        _model.value = withDerivedText(_model.value.copy(termDays = termDays))
    }

    override fun onContinueClick() {
        val current = _model.value
        output.onDraftReady(
            LoanDraft(
                productId = args.productId,
                amount = current.amount,
                termDays = current.termDays,
                promoCode = args.prefill?.promoCode,
                discountPercent = args.prefill?.discountPercent ?: 0,
            ),
        )
    }

    private fun withDerivedText(model: CalculatorComponent.Model): CalculatorComponent.Model {
        val total = calculateTotal(
            amount = model.amount,
            termDays = model.termDays,
            discountPercent = args.prefill?.discountPercent ?: 0,
        )
        return model.copy(
            amountText = moneyFormatter.format(model.amount),
            totalToRepayText = "A devolver: ${moneyFormatter.format(total)}",
        )
    }
}
