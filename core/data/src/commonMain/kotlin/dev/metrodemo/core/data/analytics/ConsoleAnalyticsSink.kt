package dev.metrodemo.core.data.analytics

import dev.metrodemo.core.common.analytics.AnalyticsSink
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@ContributesIntoSet(AppScope::class)
@Inject
class ConsoleAnalyticsSink : AnalyticsSink {

    override fun log(event: String) {
        println("[analytics:console] $event")
    }
}
