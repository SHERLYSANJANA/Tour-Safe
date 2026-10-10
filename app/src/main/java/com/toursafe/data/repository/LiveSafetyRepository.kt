package com.toursafe.data.repository

import com.toursafe.data.mock.MockData
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.data.models.UserSafetyStatus
import com.toursafe.data.remote.GdacsParser
import com.toursafe.data.remote.Http
import com.toursafe.data.remote.OverpassParser
import com.toursafe.data.remote.TtlCache
import com.toursafe.data.remote.UsgsParser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Real data, no API keys:
 *  - Alerts: USGS earthquakes + GDACS (UN/EC) cyclones, floods, volcanoes, wildfires, tsunamis.
 *  - Safe places: OpenStreetMap (Overpass) hospitals, police, fire stations, assembly points, shelters.
 *
 * Honesty rules: alerts come from official feeds, so they are marked verified; OSM places are
 * community-maintained, so the parser marks them unverified. A failed request is reported as
 * [DataSourceState.Unavailable], never as "no alerts nearby".
 *
 * Demo mode is an explicit opt-in: when [demoMode] is true the labelled demo data is returned
 * instead of live data. The two are never mixed.
 */
class LiveSafetyRepository(
    private val httpGet: suspend (String) -> String = { url -> withContext(Dispatchers.IO) { Http.get(url) } },
    private val httpPostForm: suspend (String, Map<String, String>) -> String =
        { url, form -> withContext(Dispatchers.IO) { Http.postForm(url, form) } },
    private val cache: TtlCache = TtlCache(),
    private val nowMs: () -> Long = { System.currentTimeMillis() }
) : SafetyRepository {

    override fun getUserStatus(): UserSafetyStatus = UserSafetyStatus()

    override suspend fun getAlerts(
        userLat: Double,
        userLng: Double,
        demoMode: Boolean
    ): DataSourceState<DisasterAlert> {
        if (demoMode) return DataSourceState.Success(MockData.demoAlerts)
        if (!hasFix(userLat, userLng)) return DataSourceState.NoVerifiedSource

        val (usgs, gdacs) = coroutineScope {
            val u = async { attempt { cachedGet(UsgsParser.FEED_URL, USGS_TTL_MS) } }
            val g = async { attempt { cachedGet(GdacsParser.FEED_URL, GDACS_TTL_MS) } }
            u.await() to g.await()
        }

        val usgsAlerts = usgs.mapCatching { UsgsParser.parse(it, userLat, userLng, nowMs = nowMs()) }
        val gdacsAlerts = gdacs.mapCatching { GdacsParser.parse(it, userLat, userLng, nowMs = nowMs()) }
        rethrowCancellation(usgsAlerts)
        rethrowCancellation(gdacsAlerts)

        if (usgsAlerts.isFailure && gdacsAlerts.isFailure) return DataSourceState.Unavailable

        val combined = (usgsAlerts.getOrDefault(emptyList()) + gdacsAlerts.getOrDefault(emptyList()))
            .sortedWith(compareBy<DisasterAlert> { it.severity.ordinal }.thenBy { it.distanceKm })

        return DataSourceState.Success(combined, isPartial = usgsAlerts.isFailure || gdacsAlerts.isFailure)
    }

    override suspend fun getAccommodations(
        userLat: Double,
        userLng: Double,
        demoMode: Boolean
    ): DataSourceState<SafeAccommodation> {
        if (demoMode) return DataSourceState.Success(MockData.demoAccommodations)
        if (!hasFix(userLat, userLng)) return DataSourceState.NoVerifiedSource

        val query = OverpassParser.buildQuery(userLat, userLng)
        // ~100 m grid so small GPS jitter doesn't bypass the cache.
        val key = String.format(Locale.US, "overpass:%.3f,%.3f:%d", userLat, userLng, OverpassParser.DEFAULT_RADIUS_M)

        val body: String? = cache.get(key, OVERPASS_TTL_MS) ?: run {
            var fetched: String? = null
            for (endpoint in OverpassParser.ENDPOINTS) {
                val r = attempt { httpPostForm(endpoint, mapOf("data" to query)) }
                if (r.isSuccess) {
                    fetched = r.getOrThrow()
                    break
                }
                rethrowCancellation(r)
            }
            fetched?.also { cache.put(key, it) }
        }
        if (body == null) return DataSourceState.Unavailable

        val parsed = runCatching { OverpassParser.parse(body, userLat, userLng) }
        rethrowCancellation(parsed)
        return parsed.fold(
            onSuccess = { DataSourceState.Success(it) },
            onFailure = { DataSourceState.Unavailable }
        )
    }

    // --- helpers -------------------------------------------------------------------------

    private suspend fun cachedGet(url: String, ttlMs: Long): String {
        cache.get(url, ttlMs)?.let { return it }
        return httpGet(url).also { cache.put(url, it) }
    }

    /** Like runCatching, but lets coroutine cancellation propagate. */
    private inline fun <T> attempt(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun rethrowCancellation(r: Result<*>) {
        val e = r.exceptionOrNull()
        if (e is CancellationException) throw e
    }

    private fun hasFix(lat: Double, lng: Double): Boolean =
        !(lat == 0.0 && lng == 0.0) && !lat.isNaN() && !lng.isNaN()

    private companion object {
        const val USGS_TTL_MS = 5 * 60_000L
        const val GDACS_TTL_MS = 10 * 60_000L
        const val OVERPASS_TTL_MS = 15 * 60_000L
    }
}
