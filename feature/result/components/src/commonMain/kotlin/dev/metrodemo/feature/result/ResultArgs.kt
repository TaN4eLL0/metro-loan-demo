package dev.metrodemo.feature.result

import dev.metrodemo.core.common.LoanDraft

abstract class ResultScope private constructor()

data class ResultArgs(
    val applicationId: String,
    val draft: LoanDraft,
)

interface ResultOutput {

    fun onNewLoanRequested()

    fun onRepeatRequested(draft: LoanDraft)
}
