package dev.metrodemo.core.data

import dev.metrodemo.core.common.LoanDraft
import dev.metrodemo.core.data.storage.KeyValueStorage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.delay

/**
 * Consumes the `@Named("persistent")` storage, so a wrong qualifier here would be a compile error
 * rather than a runtime surprise.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class FakeLoanApplicationRepository(
    @param:Named("persistent") private val storage: KeyValueStorage,
) : LoanApplicationRepository {

    private var counter = 0

    override suspend fun submit(draft: LoanDraft): String {
        delay(SUBMIT_DELAY_MS)
        counter++
        val id = "APP-${draft.productId.uppercase()}-$counter"
        storage.put(LAST_APPLICATION_KEY, id)
        return id
    }

    override fun lastApplicationId(): String? = storage.get(LAST_APPLICATION_KEY)

    private companion object {
        const val SUBMIT_DELAY_MS = 600L
        const val LAST_APPLICATION_KEY = "lastApplicationId"
    }
}
