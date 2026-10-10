package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
import com.toursafe.data.models.EmergencyContact
import com.toursafe.navigation.Screen
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.viewmodels.SettingsViewModel

@Composable
fun ProfileScreen(
    viewModel: SettingsViewModel,
    onNavigateToSettings: () -> Unit
) {
    val contacts by viewModel.emergencyContacts.collectAsState()
    var showAddContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.traveler_profile_title)
            )
        },
        containerColor = BackgroundNavy
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // User Details Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(AccentCyan.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Alex Morgan",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(R.string.passport_blood_type),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.current_trip),
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentCyan
                        )
                    }
                }
            }

            // App Settings Access
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy, RoundedCornerShape(14.dp))
                    .clickable { onNavigateToSettings() }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.settings_preferences),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = stringResource(R.string.configure),
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentCyan
                    )
                }
            }

            // Emergency Contacts Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.emergency_contacts_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Button(
                    onClick = { showAddContactDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(stringResource(R.string.add_contact_btn), color = Color.Black, style = MaterialTheme.typography.labelSmall)
                }
            }

            // Contacts List
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                contacts.forEach { contact ->
                    ContactRow(
                        contact = contact,
                        onDelete = { viewModel.removeContact(contact.id) }
                    )
                }
            }
        }

        if (showAddContactDialog) {
            AddContactDialog(
                onDismiss = { showAddContactDialog = false },
                onAdd = { name, relation, phone ->
                    viewModel.addContact(name, relation, phone)
                    showAddContactDialog = false
                }
            )
        }
    }
}

@Composable
private fun ContactRow(
    contact: EmergencyContact,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceNavy, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = contact.name + if (contact.isPrimary) " " + stringResource(R.string.primary_tag) else "",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (contact.isPrimary) SafetyGreen else TextPrimary
                )
                Text(
                    text = "${contact.relationship} • ${contact.phoneNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = AlertRed
                )
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceNavy,
        title = { Text(stringResource(R.string.add_contact_dialog_title), color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.full_name_label), color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    )
                )
                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text(stringResource(R.string.relationship_label), color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    )
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(R.string.phone_number_label), color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAdd(name, relation, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
            ) {
                Text(stringResource(R.string.add_contact_btn), color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = TextSecondary)
            }
        }
    )
}
