package dev.metrodemo.feature.summary

import com.arkivanov.decompose.value.Value

interface PromoDialogComponent {

    val model: Value<Model>

    fun onCodeChange(code: String)

    fun onApplyClick()

    fun onDismissClick()

    data class Model(
        val code: String = "",
        val errorText: String? = null,
        val isChecking: Boolean = false,
        val isApplyEnabled: Boolean = false,
    )
}
