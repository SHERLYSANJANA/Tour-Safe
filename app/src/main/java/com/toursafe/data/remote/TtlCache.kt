package com.toursafe.data.remote

/** Tiny in-memory cache so Home/Alerts/Map don't each hit the network for the same data. */
class TtlCache(private val now: () -> Long = { System.currentTimeMillis() }) {
    private data class Entry(val value: String, val storedAt: Long)

    private val map = HashMap<String, Entry>()

    @Synchronized
    fun get(key: String, ttlMs: Long): String? {
        val e = map[key] ?: return null
        return if (now() - e.storedAt <= ttlMs) e.value else null
    }

    @Synchronized
    fun put(key: String, value: String) {
        map[key] = Entry(value, now())
    }
}
