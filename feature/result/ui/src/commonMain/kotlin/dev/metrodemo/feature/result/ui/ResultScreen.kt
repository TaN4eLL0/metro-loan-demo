package dev.metrodemo.feature.result.ui

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
import dev.metrodemo.feature.result.ResultComponent

@Composable
fun ResultScreen(
    component: ResultComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "¡Solicitud enviada!", style = MaterialTheme.typography.headlineSmall)
        Text(text = model.productTitle, style = MaterialTheme.typography.titleMedium)
        Text(text = model.amountText, style = MaterialTheme.typography.displaySmall)
        Text(text = "ID: ${model.applicationId}", style = MaterialTheme.typography.bodyMedium)

        Button(onClick = component::onRepeatClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Repetir con los mismos datos")
        }

        TextButton(onClick = component::onNewLoanClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Nuevo préstamo")
        }
    }
}
