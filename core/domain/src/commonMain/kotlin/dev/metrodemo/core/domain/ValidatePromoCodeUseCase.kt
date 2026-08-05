package dev.metrodemo.core.domain

import dev.metrodemo.core.data.PromoCodeRepository
import dev.zacsweers.metro.Inject

@Inject
class ValidatePromoCodeUseCase(
    private val repository: PromoCodeRepository,
) {

    suspend operator fun invoke(code: String): Int? =
        code.takeIf { it.isNotBlank() }?.let { repository.discountPercent(it) }
}
