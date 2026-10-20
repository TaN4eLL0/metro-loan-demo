package dev.metrodemo.core.common

import kotlinx.serialization.Serializable

@Serializable
data class LoanDraft(
    val productId: String,
    val amount: Int,
    val termDays: Int,
    val promoCode: String? = null,
    val discountPercent: Int = 0,
)
