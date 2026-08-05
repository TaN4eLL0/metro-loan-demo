package dev.metrodemo.feature.summary

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.metrodemo.core.common.LoanDraft
import dev.metrodemo.core.common.coroutines.componentScope
import dev.metrodemo.core.common.format.MoneyFormatter
import dev.metrodemo.core.domain.CalculateTotalUseCase
import dev.metrodemo.core.domain.GetProductUseCase
import dev.metrodemo.core.domain.SubmitApplicationUseCase
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@SingleIn(SummaryScope::class)
@ContributesBinding(SummaryScope::class, binding = binding<SummaryComponent>())
@Inject
class DefaultSummaryComponent(
    componentContext: ComponentContext,
    private val args: SummaryArgs,
    private val output: SummaryOutput,
    private val promoDialogGraphFactory: PromoDialogGraph.Factory,
    private val getProduct: GetProductUseCase,
    private val submitApplication: SubmitApplicationUseCase,
    private val calculateTotal: CalculateTotalUseCase,
    private val moneyFormatter: MoneyFormatter,
) : SummaryComponent, ComponentContext by componentContext {

    private val draft = MutableValue(args.draft)

    private val _model = MutableValue(SummaryComponent.Model())
    override val model: Value<SummaryComponent.Model> = _model

    private val slotNavigation = SlotNavigation<PromoConfig>()

    private val scope = lifecycle.componentScope()

    /**
     * The dialog gets `childContext` — its own context handed to us by `childSlot`, never `this`.
     * Passing the parent component here is what produced the childSlot key collisions in android-mx.
     */
    override val promoDialog: Value<ChildSlot<*, PromoDialogComponent>> = childSlot(
        source = slotNavigation,
        serializer = PromoConfig.serializer(),
        key = "promoDialog",
        handleBackButton = true,
        childFactory = { _, childContext ->
            promoDialogGraphFactory.createPromoDialogGraph(
                componentContext = childContext,
                output = PromoDialogOutput(::onPromoFinished),
            ).component
        },
    )

    init {
        refreshModel()
        scope.launch {
            _model.value = _model.value.copy(productTitle = getProduct(args.draft.productId).title)
        }
    }

    override fun onPromoClick() {
        slotNavigation.activate(PromoConfig)
    }

    override fun onEditClick() {
        output.onEditRequested(draft.value)
    }

    override fun onConfirmClick() {
        refreshModel(isSubmitting = true)
        scope.launch {
            val applicationId = submitApplication(draft.value)
            output.onSubmitted(applicationId = applicationId, draft = draft.value)
        }
    }

    private fun onPromoFinished(code: String?, discountPercent: Int) {
        slotNavigation.dismiss()
        if (code != null) {
            draft.value = draft.value.copy(promoCode = code, discountPercent = discountPercent)
            refreshModel()
        }
    }

    private fun refreshModel(isSubmitting: Boolean = _model.value.isSubmitting) {
        val current: LoanDraft = draft.value
        val total = calculateTotal(
            amount = current.amount,
            termDays = current.termDays,
            discountPercent = current.discountPercent,
        )
        _model.value = _model.value.copy(
            amountText = moneyFormatter.format(current.amount),
            termDays = current.termDays,
            promoCode = current.promoCode,
            totalToRepayText = "A devolver: ${moneyFormatter.format(total)}",
            isSubmitting = isSubmitting,
        )
    }
}

@Serializable
private data object PromoConfig
