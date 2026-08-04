package dev.metrodemo.core.domain

import dev.zacsweers.metro.Inject

@Inject
class CalculateTotalUseCase {

    operator fun invoke(amount: Int, termDays: Int): Int =
        amount + (amount * DAILY_RATE * termDays).toInt()

    private companion object {
        const val DAILY_RATE = 0.01
    }
}
