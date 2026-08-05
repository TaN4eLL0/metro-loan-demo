package dev.metrodemo.core.data

import dev.metrodemo.core.common.LoanDraft

interface LoanApplicationRepository {

    suspend fun submit(draft: LoanDraft): String

    fun lastApplicationId(): String?
}
