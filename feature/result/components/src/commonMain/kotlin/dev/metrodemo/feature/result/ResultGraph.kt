package dev.metrodemo.feature.result

import com.arkivanov.decompose.ComponentContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(ResultScope::class)
interface ResultGraph {

    val component: ResultComponent

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {

        fun createResultGraph(
            @Provides componentContext: ComponentContext,
            @Provides args: ResultArgs,
            @Provides output: ResultOutput,
        ): ResultGraph
    }
}
