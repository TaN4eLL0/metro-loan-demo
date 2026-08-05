package dev.metrodemo.core.data

interface PromoCodeRepository {

    suspend fun discountPercent(code: String): Int?
}
