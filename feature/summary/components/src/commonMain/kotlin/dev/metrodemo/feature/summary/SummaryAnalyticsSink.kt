package dev.metrodemo.feature.summary

import dev.metrodemo.core.common.analytics.AnalyticsSink
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@ContributesIntoSet(AppScope::class)
@Inject
class SummaryAnalyticsSink : AnalyticsSink {

    override fun log(event: String) {
        println("[analytics:summary] $event")
    }
}
