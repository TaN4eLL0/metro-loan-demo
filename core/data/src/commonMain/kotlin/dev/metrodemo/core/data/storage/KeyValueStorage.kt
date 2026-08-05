package dev.metrodemo.core.data.storage

interface KeyValueStorage {

    fun put(key: String, value: String)

    fun get(key: String): String?
}
