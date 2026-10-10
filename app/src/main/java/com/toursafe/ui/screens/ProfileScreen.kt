package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
import com.toursafe.data.models.EmergencyContact
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.viewmodels.SettingsViewModel

@Composable
fun ProfileScreen(
    viewModel: SettingsViewModel,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSettings(context)
    }

    val contacts by viewModel.emergencyContacts.collectAsState()
    val profileName by viewModel.profileName.collectAsState()

    var showAddContactDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.traveler_profile_title)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .clickable { showEditProfileDialog = true }
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = profileName.ifBlank { "Tap to Set Profile Name" },
                                style = MaterialTheme.typography.titleLarge,
                                color = if (profileName.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.profile_edit), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // App Settings Access
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
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
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.settings_preferences),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = stringResource(R.string.configure),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = { showAddContactDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(stringResource(R.string.add_contact_btn), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                }
            }

            // Contacts List
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (contacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_contacts_yet),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    contacts.forEach { contact ->
                        ContactRow(
                            contact = contact,
                            onDelete = { viewModel.deleteEmergencyContact(context, contact.id) }
                        )
                    }
                }
            }
        }

        if (showEditProfileDialog) {
            EditProfileNameDialog(
                currentName = profileName,
                onDismiss = { showEditProfileDialog = false },
                onSave = { newName ->
                    viewModel.saveProfileName(context, newName)
                    showEditProfileDialog = false
                }
            )
        }

        if (showAddContactDialog) {
            AddContactDialog(
                onDismiss = { showAddContactDialog = false },
                onAdd = { name, relation, phone ->
                    viewModel.addEmergencyContact(context, name, relation, phone)
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
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
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
                    color = if (contact.isPrimary) MaterialTheme.tourSafe.safe else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${contact.relationship} • ${contact.phoneNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EditProfileNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.profile_name_title), color = MaterialTheme.colorScheme.onSurface) },
        text = {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text(stringResource(R.string.full_name_label), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(nameInput.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
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
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.add_contact_dialog_title), color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.full_name_label), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text(stringResource(R.string.relationship_label), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(R.string.phone_number_label), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.add_contact_btn), color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
