package dev.metrodemo.feature.calculator

import dev.metrodemo.core.common.LoanDraft

data class CalculatorArgs(
    val productId: String,
    val prefill: LoanDraft? = null,
)
