package dev.metrodemo.feature.calculator

import com.arkivanov.decompose.value.Value

interface CalculatorComponent {

    val model: Value<Model>

    fun onAmountChange(amount: Int)

    fun onTermChange(termDays: Int)

    fun onContinueClick()

    data class Model(
        val productTitle: String = "",
        val amount: Int = 0,
        val minAmount: Int = 0,
        val maxAmount: Int = 0,
        val termDays: Int = 0,
        val minTermDays: Int = 0,
        val maxTermDays: Int = 0,
        val amountText: String = "",
        val totalToRepayText: String = "",
        val isContinueEnabled: Boolean = false,
    )
}
