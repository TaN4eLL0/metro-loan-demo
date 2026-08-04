package dev.metrodemo.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.metrodemo.core.data.Product
import dev.metrodemo.shared.di.AppGraph

/**
 * Temporary P1 screen. It exists to prove the wiring end to end:
 * `@Provides` factory input, an `@Inject` use case from `:core:domain`, a `@SingleIn` formatter
 * from `:core:common`, and a `@ContributesBinding` repository from `:core:data` that `:shared`
 * never names. P3 replaces it with the real Decompose root.
 */
@Composable
fun GraphSmokeScreen(
    graph: AppGraph,
    modifier: Modifier = Modifier,
) {
    var products by remember { mutableStateOf(emptyList<Product>()) }

    LaunchedEffect(graph) {
        products = graph.productsRepository.products()
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Metro graph is alive", style = MaterialTheme.typography.headlineSmall)
            Text(text = "platform: ${graph.platform.name}")
            Text(text = "formatter: ${graph.moneyFormatter.format(15_000)}")
            Text(text = "useCase: ${graph.calculateTotal(amount = 15_000, termDays = 30)}")
            Text(text = "sameInstance: ${graph.moneyFormatter === graph.moneyFormatter}")

            Text(
                text = "products (${products.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
            products.forEach { product ->
                Text(text = "${product.title}: ${graph.moneyFormatter.format(product.maxAmount)}")
            }
        }
    }
}
