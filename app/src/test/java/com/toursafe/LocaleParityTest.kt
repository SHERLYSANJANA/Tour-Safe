package com.toursafe

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LocaleParityTest {

    private fun extractKeysFromXml(file: File): Set<String> {
        if (!file.exists()) return emptySet()
        val regex = Regex("""<string\s+[^>]*name="([^"]+)"[^>]*>""")
        return regex.findAll(file.readText()).map { it.groupValues[1] }.toSet()
    }

    @Test
    fun allLocales_haveCompleteStringKeyParity() {
        val projectDir = File(System.getProperty("user.dir") ?: ".")
        val resDir = File(projectDir, "src/main/res")

        val baseFile = File(resDir, "values/strings.xml")
        assertTrue("Base strings.xml must exist", baseFile.exists())

        val baseKeys = extractKeysFromXml(baseFile)
        assertTrue("Base strings.xml must not be empty", baseKeys.isNotEmpty())

        val targetFolders = listOf(
            "values-es",
            "values-fr",
            "values-de",
            "values-zh",
            "values-ja",
            "values-th",
            "values-hi"
        )

        val missingReport = mutableMapOf<String, Set<String>>()

        for (folder in targetFolders) {
            val file = File(resDir, "$folder/strings.xml")
            assertTrue("Locale file $folder/strings.xml must exist", file.exists())
            val localeKeys = extractKeysFromXml(file)
            val missing = baseKeys - localeKeys
            if (missing.isNotEmpty()) {
                missingReport[folder] = missing
            }
        }

        assertTrue(
            "The following locales are missing keys present in values/strings.xml:\n$missingReport",
            missingReport.isEmpty()
        )
    }
}
