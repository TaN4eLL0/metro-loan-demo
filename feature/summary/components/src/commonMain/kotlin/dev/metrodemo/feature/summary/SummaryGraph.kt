package dev.metrodemo.feature.summary

import com.arkivanov.decompose.ComponentContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(SummaryScope::class)
interface SummaryGraph {

    val component: SummaryComponent

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {

        fun createSummaryGraph(
            @Provides componentContext: ComponentContext,
            @Provides args: SummaryArgs,
            @Provides output: SummaryOutput,
        ): SummaryGraph
    }
}

/**
 * A graph extension of a graph extension: the dialog's graph hangs off [SummaryGraph], not off
 * `AppGraph`. `@ContributesTo(SummaryScope::class)` puts this factory on the Summary graph, so the
 * dialog can only ever be created from the screen that owns it.
 */
@GraphExtension(PromoDialogScope::class)
interface PromoDialogGraph {

    val component: PromoDialogComponent

    @ContributesTo(SummaryScope::class)
    @GraphExtension.Factory
    interface Factory {

        fun createPromoDialogGraph(
            @Provides componentContext: ComponentContext,
            @Provides output: PromoDialogOutput,
        ): PromoDialogGraph
    }
}
