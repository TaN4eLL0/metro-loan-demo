package dev.metrodemo.feature.calculator

import dev.metrodemo.core.common.LoanDraft

interface CalculatorOutput {

    fun onDraftReady(draft: LoanDraft)
}
