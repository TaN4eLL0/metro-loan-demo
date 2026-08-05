package dev.metrodemo.core.data.storage

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Two implementations of one interface, told apart by `@Named` — the direct equivalent of our
 * `get<LocalStorage>(named(LocalStorageType.DEFAULT))` in android-mx.
 *
 * A `@BindingContainer` is used instead of `@ContributesBinding` because the qualifier belongs to
 * the binding, not to the class.
 */
@ContributesTo(AppScope::class)
@BindingContainer
object StorageBindings {

    @Provides
    @SingleIn(AppScope::class)
    @Named("memory")
    fun memoryStorage(): KeyValueStorage = MapKeyValueStorage(prefix = "memory")

    @Provides
    @SingleIn(AppScope::class)
    @Named("persistent")
    fun persistentStorage(): KeyValueStorage = MapKeyValueStorage(prefix = "persistent")
}

private class MapKeyValueStorage(private val prefix: String) : KeyValueStorage {

    private val entries = mutableMapOf<String, String>()

    override fun put(key: String, value: String) {
        entries["$prefix:$key"] = value
    }

    override fun get(key: String): String? = entries["$prefix:$key"]
}
