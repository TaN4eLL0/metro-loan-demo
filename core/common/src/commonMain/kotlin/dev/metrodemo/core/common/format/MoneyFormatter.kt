package dev.metrodemo.core.common.format

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
class MoneyFormatter {

    fun format(amount: Int): String {
        val grouped = amount.toString()
            .reversed()
            .chunked(GROUP_SIZE)
            .joinToString(",")
            .reversed()
        return "$grouped MXN"
    }

    private companion object {
        const val GROUP_SIZE = 3
    }
}
