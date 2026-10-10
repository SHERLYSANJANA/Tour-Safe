package com.toursafe.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.LanguageOption
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.util.LocaleManager
import com.toursafe.viewmodels.SettingsViewModel

@Composable
fun LanguageSelectionScreen(
    viewModel: SettingsViewModel,
    onLanguageConfirmed: () -> Unit
) {
    val context = LocalContext.current

    var selectedLangCode by remember { mutableStateOf(LocaleManager.getPersistedLanguage(context)) }

    val languages = remember(selectedLangCode) {
        MockData.languages.map { l ->
            l.copy(isSelected = l.code == selectedLangCode)
        }
    }

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.select_language)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // weight(1f) is required: without it the LazyColumn takes all the height and the
            // Confirm button below it is pushed off-screen, so the user can never continue.
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.select_language_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(languages) { lang ->
                        LanguageItemCard(
                            language = lang,
                            onSelect = {
                                selectedLangCode = lang.code
                                // Same flow as before: pick a language -> save it -> continue.
                                viewModel.selectLanguage(lang.code, context)
                                onLanguageConfirmed()
                                // Android 13+ re-applies the locale by itself; older versions
                                // need the activity restarted so the new language shows.
                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
                                    (context as? Activity)?.recreate()
                                }
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onLanguageConfirmed,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.confirm_language),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun LanguageItemCard(
    language: LanguageOption,
    onSelect: () -> Unit
) {
    val isSelected = language.isSelected
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = language.flagEmoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = language.nativeName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
