package com.toursafe.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/*
 * TourSafe palette.
 *
 * Light-first: deep forest green on a warm off-white background, white surfaces.
 * Red is reserved for emergency actions and critical warnings (Material `error` roles) and
 * is never used as a decorative brand color.
 *
 * Screens must read colors through MaterialTheme.colorScheme or MaterialTheme.tourSafe,
 * never from the raw values below, so that light and dark themes stay consistent.
 */

// ---- Light ----
internal val LightPrimary = Color(0xFF1B5E4B)
internal val LightOnPrimary = Color(0xFFFFFFFF)
internal val LightPrimaryContainer = Color(0xFFD7EBE2)
internal val LightOnPrimaryContainer = Color(0xFF0B2E24)
internal val LightSecondary = Color(0xFF2F6F73)
internal val LightSecondaryContainer = Color(0xFFDCEBEC)
internal val LightBackground = Color(0xFFF8F6F1)
internal val LightSurface = Color(0xFFFFFFFF)
internal val LightSurfaceVariant = Color(0xFFEEF0EB)
internal val LightOnSurface = Color(0xFF16201C)
internal val LightOnSurfaceVariant = Color(0xFF55605A)
internal val LightOutline = Color(0xFF8A938E)
internal val LightOutlineVariant = Color(0xFFDADFD9)
internal val LightError = Color(0xFFB3261E)
internal val LightOnError = Color(0xFFFFFFFF)
internal val LightErrorContainer = Color(0xFFFBE4E1)
internal val LightOnErrorContainer = Color(0xFF410E0B)

// ---- Dark ----
internal val DarkPrimary = Color(0xFF7CCBAE)
internal val DarkOnPrimary = Color(0xFF05281D)
internal val DarkPrimaryContainer = Color(0xFF1F4A3C)
internal val DarkOnPrimaryContainer = Color(0xFFD7EBE2)
internal val DarkSecondary = Color(0xFF8CC9CD)
internal val DarkSecondaryContainer = Color(0xFF214A4D)
internal val DarkBackground = Color(0xFF0E1512)
internal val DarkSurface = Color(0xFF16201C)
internal val DarkSurfaceVariant = Color(0xFF25322C)
internal val DarkOnSurface = Color(0xFFE6EBE8)
internal val DarkOnSurfaceVariant = Color(0xFFA7B2AC)
internal val DarkOutline = Color(0xFF6F7B75)
internal val DarkOutlineVariant = Color(0xFF33423B)
internal val DarkError = Color(0xFFF2877F)
internal val DarkOnError = Color(0xFF3B0A07)
internal val DarkErrorContainer = Color(0xFF4A1512)
internal val DarkOnErrorContainer = Color(0xFFFBE4E1)

/**
 * Semantic colors that Material 3's ColorScheme has no role for.
 * Danger/critical uses ColorScheme.error / errorContainer instead of a duplicate here.
 */
@Immutable
data class TourSafeColors(
    val safe: Color,
    val safeContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val info: Color,
    val infoContainer: Color,
    /** Hospitals, police, fire stations on the map. */
    val service: Color,
    /** The user's own position marker (conventional map blue). */
    val userLocation: Color
)

internal val LightTourSafeColors = TourSafeColors(
    safe = Color(0xFF1E7A46),
    safeContainer = Color(0xFFDDF1E4),
    warning = Color(0xFF8A5A00),
    warningContainer = Color(0xFFFCEFD0),
    info = Color(0xFF2F6F73),
    infoContainer = Color(0xFFDCEBEC),
    service = Color(0xFF5B4B9A),
    userLocation = Color(0xFF1A73E8)
)

internal val DarkTourSafeColors = TourSafeColors(
    safe = Color(0xFF6FD39A),
    safeContainer = Color(0xFF123824),
    warning = Color(0xFFF2B84B),
    warningContainer = Color(0xFF3D2A05),
    info = Color(0xFF8CC9CD),
    infoContainer = Color(0xFF214A4D),
    service = Color(0xFFB7A8F0),
    userLocation = Color(0xFF8AB4F8)
)

val LocalTourSafeColors = staticCompositionLocalOf { LightTourSafeColors }

val MaterialTheme.tourSafe: TourSafeColors
    @Composable
    @ReadOnlyComposable
    get() = LocalTourSafeColors.current
