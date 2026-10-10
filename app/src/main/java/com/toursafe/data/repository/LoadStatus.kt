package com.toursafe.data.repository

/** What a screen should say about a data list that came from a [SafetyRepository]. */
enum class LoadStatus {
    /** Nothing requested yet (no GPS fix, demo off). */
    IDLE,
    LOADING,
    /** Sources answered. The list may legitimately be empty ("nothing nearby"). */
    LOADED,
    /** Some sources answered, others failed: the list may be incomplete. */
    PARTIAL,
    /** All sources failed (offline, timeout, server error). The list is empty because we don't know. */
    UNAVAILABLE
}

internal fun <T> DataSourceState<T>.toStatus(): LoadStatus = when (this) {
    is DataSourceState.NoVerifiedSource -> LoadStatus.IDLE
    is DataSourceState.Unavailable -> LoadStatus.UNAVAILABLE
    is DataSourceState.Success -> if (isPartial) LoadStatus.PARTIAL else LoadStatus.LOADED
}

internal fun <T> DataSourceState<T>.items(): List<T> = when (this) {
    is DataSourceState.Success -> data
    else -> emptyList()
}

/**
 * Combines the status of two independent loads (e.g. alerts + places on the Map screen).
 * Any IDLE/LOADING wins first so the UI never claims a result before everything answered;
 * both failing is UNAVAILABLE; one failing (or either partial) is PARTIAL.
 */
fun combineStatus(a: LoadStatus, b: LoadStatus): LoadStatus = when {
    a == LoadStatus.LOADING || b == LoadStatus.LOADING -> LoadStatus.LOADING
    a == LoadStatus.IDLE || b == LoadStatus.IDLE -> LoadStatus.IDLE
    a == LoadStatus.UNAVAILABLE && b == LoadStatus.UNAVAILABLE -> LoadStatus.UNAVAILABLE
    a == LoadStatus.UNAVAILABLE || b == LoadStatus.UNAVAILABLE ||
        a == LoadStatus.PARTIAL || b == LoadStatus.PARTIAL -> LoadStatus.PARTIAL
    else -> LoadStatus.LOADED
}
