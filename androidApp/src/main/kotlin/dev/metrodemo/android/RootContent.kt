package dev.metrodemo.android

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import dev.metrodemo.feature.calculator.ui.CalculatorScreen
import dev.metrodemo.feature.productlist.ui.ProductListScreen
import dev.metrodemo.feature.result.ui.ResultScreen
import dev.metrodemo.feature.summary.ui.SummaryScreen
import dev.metrodemo.shared.RootComponent

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Children(
            stack = component.stack,
            animation = stackAnimation(fade(animationSpec = tween(durationMillis = 200))),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.ProductList -> ProductListScreen(component = instance.component)
                is RootComponent.Child.Calculator -> CalculatorScreen(component = instance.component)
                is RootComponent.Child.Summary -> SummaryScreen(component = instance.component)
                is RootComponent.Child.Result -> ResultScreen(component = instance.component)
            }
        }
    }
}
