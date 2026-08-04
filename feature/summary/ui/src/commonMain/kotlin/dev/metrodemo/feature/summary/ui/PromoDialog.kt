package dev.metrodemo.feature.summary.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.metrodemo.feature.summary.PromoDialogComponent

@Composable
fun PromoDialog(component: PromoDialogComponent) {
    val model by component.model.subscribeAsState()

    AlertDialog(
        onDismissRequest = component::onDismissClick,
        title = { Text(text = "Código promocional") },
        text = {
            OutlinedTextField(
                value = model.code,
                onValueChange = component::onCodeChange,
                isError = model.errorText != null,
                supportingText = model.errorText?.let { { Text(text = it) } },
                enabled = !model.isChecking,
            )
        },
        confirmButton = {
            TextButton(onClick = component::onApplyClick, enabled = model.isApplyEnabled) {
                Text(text = "Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = component::onDismissClick) {
                Text(text = "Cancelar")
            }
        },
    )
}
