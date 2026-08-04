package dev.metrodemo.feature.productlist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.metrodemo.feature.productlist.ProductListComponent

@Composable
fun ProductListScreen(
    component: ProductListComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Elige un producto", style = MaterialTheme.typography.headlineSmall)

        if (model.isLoading) {
            CircularProgressIndicator()
        } else {
            model.products.forEach { product ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .clickable { component.onProductClick(product.id) }
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(text = product.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = product.amountRange, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
