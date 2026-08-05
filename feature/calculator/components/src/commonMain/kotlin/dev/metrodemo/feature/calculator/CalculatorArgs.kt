package dev.metrodemo.feature.calculator

import dev.metrodemo.core.common.LoanDraft

/**
 * Input parameters. [prefill] is how the screen is re-entered from Summary with an already edited
 * draft — the backward parameter case.
 */
data class CalculatorArgs(
    val productId: String,
    val prefill: LoanDraft? = null,
)
