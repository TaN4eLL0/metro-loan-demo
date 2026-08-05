package dev.metrodemo.core.common.analytics

interface AnalyticsSink {

    fun log(event: String)
}
