package dev.metrodemo.core.domain

import dev.zacsweers.metro.Inject

@Inject
class CalculateTotalUseCase {

    operator fun invoke(amount: Int, termDays: Int, discountPercent: Int = 0): Int {
        val interest = amount * DAILY_RATE * termDays
        val discounted = interest * (PERCENT_BASE - discountPercent) / PERCENT_BASE
        return amount + discounted.toInt()
    }

    private companion object {
        const val DAILY_RATE = 0.01
        const val PERCENT_BASE = 100
    }
}
