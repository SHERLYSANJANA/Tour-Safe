package com.toursafe.util

object TimeText {
    fun relative(thenMs: Long?, nowMs: Long = System.currentTimeMillis()): String {
        if (thenMs == null) return "Time unknown"
        val diff = (nowMs - thenMs).coerceAtLeast(0L)
        val minutes = diff / 60_000L
        val hours = minutes / 60
        val days = hours / 24
        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes min${if (minutes == 1L) "" else "s"} ago"
            hours < 24 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
            else -> "$days day${if (days == 1L) "" else "s"} ago"
        }
    }
}
