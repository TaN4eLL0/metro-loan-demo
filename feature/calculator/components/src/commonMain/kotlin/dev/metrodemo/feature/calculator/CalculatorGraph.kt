package dev.metrodemo.feature.calculator

import com.arkivanov.decompose.ComponentContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(CalculatorScope::class)
interface CalculatorGraph {

    val component: CalculatorComponent

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {

        fun createCalculatorGraph(
            @Provides componentContext: ComponentContext,
            @Provides args: CalculatorArgs,
            @Provides output: CalculatorOutput,
        ): CalculatorGraph
    }
}
