package com.toursafe.util

import android.app.LocaleManager as SystemLocaleManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleManager {

    private const val PREFS_NAME = "toursafe_locale_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    fun getLocaleForLanguageCode(languageCode: String): Locale {
        return when (languageCode) {
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "es" -> Locale("es")
            "fr" -> Locale("fr")
            "de" -> Locale("de")
            "ja" -> Locale("ja")
            "th" -> Locale("th")
            "hi" -> Locale("hi")
            else -> Locale.ENGLISH
        }
    }

    fun getPersistedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLocale(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()

        val locale = getLocaleForLanguageCode(languageCode)
        Locale.setDefault(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val systemLocaleManager = context.getSystemService(SystemLocaleManager::class.java)
            systemLocaleManager?.applicationLocales = LocaleList(locale)
        }
    }

    fun wrapContext(context: Context): ContextWrapper {
        val lang = getPersistedLanguage(context)
        val locale = getLocaleForLanguageCode(lang)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
            ContextWrapper(context.createConfigurationContext(config))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            ContextWrapper(context)
        }
    }
}
