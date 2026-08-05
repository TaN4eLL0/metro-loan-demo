package dev.metrodemo.core.common

import kotlinx.serialization.Serializable

/**
 * The value that travels between screens. It lives in `:core:common` because features must not
 * depend on each other — Root routes it, they only hand it over.
 */
@Serializable
data class LoanDraft(
    val productId: String,
    val amount: Int,
    val termDays: Int,
    val promoCode: String? = null,
    val discountPercent: Int = 0,
)
