package dev.metrodemo.feature.summary

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

interface SummaryComponent {

    val model: Value<Model>

    val promoDialog: Value<ChildSlot<*, PromoDialogComponent>>

    fun onPromoClick()

    fun onEditClick()

    fun onConfirmClick()

    data class Model(
        val productTitle: String = "",
        val amountText: String = "",
        val termDays: Int = 0,
        val promoCode: String? = null,
        val totalToRepayText: String = "",
        val isSubmitting: Boolean = false,
    )
}
