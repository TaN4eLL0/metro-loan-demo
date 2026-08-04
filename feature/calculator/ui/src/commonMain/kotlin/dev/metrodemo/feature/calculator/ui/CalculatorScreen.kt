package dev.metrodemo.feature.calculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.metrodemo.feature.calculator.CalculatorComponent

@Composable
fun CalculatorScreen(
    component: CalculatorComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = model.productTitle, style = MaterialTheme.typography.headlineSmall)
        Text(text = model.amountText, style = MaterialTheme.typography.displaySmall)

        if (model.maxAmount > model.minAmount) {
            Slider(
                value = model.amount.toFloat(),
                onValueChange = { component.onAmountChange(it.toInt()) },
                valueRange = model.minAmount.toFloat()..model.maxAmount.toFloat(),
            )
        }

        Text(text = "${model.termDays} días", style = MaterialTheme.typography.titleMedium)

        if (model.maxTermDays > model.minTermDays) {
            Slider(
                value = model.termDays.toFloat(),
                onValueChange = { component.onTermChange(it.toInt()) },
                valueRange = model.minTermDays.toFloat()..model.maxTermDays.toFloat(),
            )
        }

        Text(text = model.totalToRepayText, style = MaterialTheme.typography.bodyLarge)

        Button(
            onClick = component::onContinueClick,
            enabled = model.isContinueEnabled,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Continuar")
        }
    }
}
