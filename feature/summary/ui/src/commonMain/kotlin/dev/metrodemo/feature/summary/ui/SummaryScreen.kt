package dev.metrodemo.feature.summary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.metrodemo.feature.summary.SummaryComponent

@Composable
fun SummaryScreen(
    component: SummaryComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()
    val promoSlot by component.promoDialog.subscribeAsState()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Resumen", style = MaterialTheme.typography.headlineSmall)
        Text(text = model.productTitle, style = MaterialTheme.typography.titleMedium)
        Text(text = model.amountText, style = MaterialTheme.typography.displaySmall)
        Text(text = "${model.termDays} días", style = MaterialTheme.typography.bodyLarge)
        Text(text = model.totalToRepayText, style = MaterialTheme.typography.bodyLarge)

        TextButton(onClick = component::onPromoClick) {
            Text(text = model.promoCode?.let { "Promo: $it" } ?: "Agregar código promocional")
        }

        Button(
            onClick = component::onConfirmClick,
            enabled = !model.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Confirmar")
        }

        TextButton(onClick = component::onEditClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Editar monto")
        }
    }

    promoSlot.child?.instance?.let { dialogComponent ->
        PromoDialog(component = dialogComponent)
    }
}
