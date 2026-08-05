package dev.metrodemo.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.delay

@ContributesBinding(AppScope::class)
@Inject
class FakePromoCodeRepository : PromoCodeRepository {

    override suspend fun discountPercent(code: String): Int? {
        delay(CHECK_DELAY_MS)
        return DISCOUNTS[code.uppercase()]
    }

    private companion object {
        const val CHECK_DELAY_MS = 500L

        val DISCOUNTS = mapOf(
            "METRO" to 20,
            "KOIN" to 5,
        )
    }
}
