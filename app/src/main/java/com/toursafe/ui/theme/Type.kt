package com.toursafe.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/*
 * Material 3 type scale. No colors are set here: text takes LocalContentColor so it adapts to
 * light/dark and to whatever container it sits on. Line heights are generous (~1.4x) so that
 * Thai and Devanagari (Hindi) diacritics are not clipped.
 */
private fun style(weight: FontWeight, size: Int, line: Int, spacing: Double = 0.0) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = spacing.sp
)

val Typography = Typography(
    headlineLarge = style(FontWeight.Bold, 28, 38),
    headlineMedium = style(FontWeight.Bold, 24, 33),
    headlineSmall = style(FontWeight.SemiBold, 20, 28),
    titleLarge = style(FontWeight.SemiBold, 18, 26),
    titleMedium = style(FontWeight.SemiBold, 16, 23, 0.1),
    titleSmall = style(FontWeight.SemiBold, 14, 21, 0.1),
    bodyLarge = style(FontWeight.Normal, 16, 24, 0.2),
    bodyMedium = style(FontWeight.Normal, 14, 21, 0.2),
    bodySmall = style(FontWeight.Normal, 12, 18, 0.3),
    labelLarge = style(FontWeight.SemiBold, 14, 20, 0.1),
    labelMedium = style(FontWeight.Medium, 12, 18, 0.4),
    labelSmall = style(FontWeight.Medium, 11, 16, 0.4)
)
