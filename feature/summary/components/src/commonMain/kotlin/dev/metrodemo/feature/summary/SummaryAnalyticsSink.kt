package dev.metrodemo.feature.summary

import dev.metrodemo.core.common.analytics.AnalyticsSink
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

/**
 * The second contributor to `Set<AnalyticsSink>`, from a feature module this time. `:core:domain`
 * consumes the set and knows about neither implementation.
 */
@ContributesIntoSet(AppScope::class)
@Inject
class SummaryAnalyticsSink : AnalyticsSink {

    override fun log(event: String) {
        println("[analytics:summary] $event")
    }
}
