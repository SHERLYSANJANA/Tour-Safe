package com.toursafe.util

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleManager {

    private const val PREFS_NAME = "toursafe_locale_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    fun getPersistedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLocale(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()

        val appLocales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }

    fun wrapContext(context: Context): ContextWrapper {
        val lang = getPersistedLanguage(context)
        val locale = when (lang) {
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "es" -> Locale("es")
            "fr" -> Locale("fr")
            "de" -> Locale("de")
            "ja" -> Locale("ja")
            "th" -> Locale("th")
            "hi" -> Locale("hi")
            else -> Locale.ENGLISH
        }
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
