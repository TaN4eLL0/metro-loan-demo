package dev.metrodemo.core.domain

import dev.metrodemo.core.common.LoanDraft
import dev.metrodemo.core.common.analytics.AnalyticsSink
import dev.metrodemo.core.data.LoanApplicationRepository
import dev.zacsweers.metro.Inject

@Inject
class SubmitApplicationUseCase(
    private val repository: LoanApplicationRepository,
    private val analyticsSinks: Set<AnalyticsSink>,
) {

    suspend operator fun invoke(draft: LoanDraft): String {
        analyticsSinks.forEach { sink ->
            sink.log("submit product=${draft.productId} amount=${draft.amount} promo=${draft.promoCode}")
        }
        return repository.submit(draft)
    }
}
