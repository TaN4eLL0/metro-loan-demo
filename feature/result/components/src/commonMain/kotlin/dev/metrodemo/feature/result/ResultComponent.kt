package dev.metrodemo.feature.result

import com.arkivanov.decompose.value.Value

interface ResultComponent {

    val model: Value<Model>

    fun onNewLoanClick()

    fun onRepeatClick()

    data class Model(
        val applicationId: String = "",
        val productTitle: String = "",
        val amountText: String = "",
    )
}
