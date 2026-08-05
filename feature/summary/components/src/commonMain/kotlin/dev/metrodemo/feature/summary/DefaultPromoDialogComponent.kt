package dev.metrodemo.feature.summary

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.metrodemo.core.common.coroutines.componentScope
import dev.metrodemo.core.domain.ValidatePromoCodeUseCase
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.launch

@SingleIn(PromoDialogScope::class)
@ContributesBinding(PromoDialogScope::class, binding = binding<PromoDialogComponent>())
@Inject
class DefaultPromoDialogComponent(
    componentContext: ComponentContext,
    private val output: PromoDialogOutput,
    private val validatePromoCode: ValidatePromoCodeUseCase,
) : PromoDialogComponent, ComponentContext by componentContext {

    private val _model = MutableValue(PromoDialogComponent.Model())
    override val model: Value<PromoDialogComponent.Model> = _model

    private val scope = lifecycle.componentScope()

    override fun onCodeChange(code: String) {
        _model.value = _model.value.copy(
            code = code,
            errorText = null,
            isApplyEnabled = code.isNotBlank(),
        )
    }

    override fun onApplyClick() {
        val code = _model.value.code
        _model.value = _model.value.copy(isChecking = true, isApplyEnabled = false)

        scope.launch {
            val discount = validatePromoCode(code)
            if (discount == null) {
                _model.value = _model.value.copy(
                    isChecking = false,
                    isApplyEnabled = true,
                    errorText = "Código no válido",
                )
            } else {
                output.onFinished(code = code, discountPercent = discount)
            }
        }
    }

    override fun onDismissClick() {
        output.onFinished(code = null, discountPercent = 0)
    }
}
