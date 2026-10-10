package com.toursafe.data.models

data class LanguageOption(
    val code: String,
    val name: String,
    val nativeName: String,
    val flagEmoji: String,
    val isSelected: Boolean = false
)
