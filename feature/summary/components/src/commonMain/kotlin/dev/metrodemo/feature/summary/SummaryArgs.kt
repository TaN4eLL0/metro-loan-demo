package dev.metrodemo.feature.summary

import dev.metrodemo.core.common.LoanDraft

data class SummaryArgs(val draft: LoanDraft)

interface SummaryOutput {

    fun onEditRequested(draft: LoanDraft)

    fun onSubmitted(applicationId: String, draft: LoanDraft)
}

fun interface PromoDialogOutput {

    fun onFinished(code: String?, discountPercent: Int)
}
